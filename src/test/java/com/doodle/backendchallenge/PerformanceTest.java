package com.doodle.backendchallenge;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("performance-test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = PerformanceTest.DockerPostgreDataSourceInitializer.class)
@ExtendWith(SpringExtension.class)
@Testcontainers
class PerformanceTest {

  private static final int CALENDAR_REQUESTS_THREADS = 3;
  private static final int CALENDAR_REQUESTS_PER_THREAD = 500;
  private static final int CALENDAR_REQUESTS_TIMEOUT_MINUTES = 10;
  private static final int TOTAL_USERS = 50;
  private static final int TOTAL_SLOTS = 10000;
  private static final int TOTAL_MEETINGS = 1000;
  private static final int MEETING_SIZE_MIN = 3;
  private static final int MEETING_SIZE_MAX = 10;
  private static final long DB_CPU_PERCENT = 1;
  private static final long DB_MEM = 50_000_000;

  private static final long SLOT_TIMESPAN_DAYS = 365 * 5;
  private static final long ONE_HOUR_MILLIS = 3600000;
  private static final DateFormat MONTH_FORMATTER = new SimpleDateFormat("yyyy-MM");

  public static PostgreSQLContainer<?> postgreDBContainer =
      new PostgreSQLContainer<>("postgres:13")
          .withDatabaseName("backend-challenge")
          .withUsername("doodle")
          .withPassword("doodle")
          .withCreateContainerCmdModifier(
              cmd ->
                  Objects.requireNonNull(cmd.getHostConfig())
                      .withCpuPercent(DB_CPU_PERCENT)
                      .withMemory(DB_MEM));

  static {
    postgreDBContainer.withClasspathResourceMapping(
        "init.sql", "/docker-entrypoint-initdb.d/init.sql", BindMode.READ_ONLY);
    postgreDBContainer.start();
  }

  public static class DockerPostgreDataSourceInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
          applicationContext,
          "spring.datasource.url=" + postgreDBContainer.getJdbcUrl(),
          "spring.datasource.username=" + postgreDBContainer.getUsername(),
          "spring.datasource.password=" + postgreDBContainer.getPassword());
    }
  }

  @Autowired private WebApplicationContext webApplicationContext;

  @LocalServerPort private int port;

  private String uri;

  public String getBaseUrl() {
    return uri;
  }

  @PostConstruct
  public void init() {
    uri = "http://localhost:" + port;
  }

  @BeforeEach
  public void before() {
    System.out.println("Setting up integration test class " + this.getClass().getSimpleName());
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }

  @Test
  void testPerformance() throws InterruptedException {
    long startTime = System.nanoTime();

    List<UUID> createdUserIds = createUsers();
    long usersCreationTime = System.nanoTime();

    List<UUID> createdSlotIds = createSlots();
    long slotsCreationTime = System.nanoTime();

    createMeetings(createdUserIds, createdSlotIds);
    long meetingsCreationTime = System.nanoTime();

    long averageCalendarResponseTime = makeCalendarRequests();
    long calendarReadingTime = System.nanoTime();

    System.out.println("Users creation time: " + formatNanos(usersCreationTime - startTime));
    System.out.println(
        "Slots creation time: " + formatNanos(slotsCreationTime - usersCreationTime));
    System.out.println(
        "Meetings creation time: " + formatNanos(meetingsCreationTime - slotsCreationTime));
    System.out.println(
        "Calendar reading time: " + formatNanos(calendarReadingTime - meetingsCreationTime));
    System.out.println("Total time: " + formatNanos(calendarReadingTime - startTime));
    System.out.println("Average calendar response time: " + averageCalendarResponseTime);

    assertThat(averageCalendarResponseTime, is(lessThan(200L)));
  }

  private List<UUID> createUsers() {
    List<UUID> userIds = new LinkedList<>();
    for (int i = 0; i < TOTAL_USERS; i++) {
      String userId =
          given()
              .contentType(ContentType.JSON)
              .body(String.format("{\"name\":\"%s\"}", generateRandomString()))
              .post(getBaseUrl() + "/users")
              .then()
              .extract()
              .path("id");
      userIds.add(UUID.fromString(userId));
    }
    return new ArrayList<>(userIds);
  }

  private List<UUID> createSlots() {
    System.out.println("Creating slots... It would take some time. Please be patient.");
    List<UUID> slotIds = new LinkedList<>();
    int slotsCreationSkipCount = 0;
    for (int i = 0; i < TOTAL_SLOTS; i++) {
      List<String> timeSlot = generateTimeSlot();
      Response response =
          given()
              .contentType(ContentType.JSON)
              .body(
                  String.format(
                      "{\"startAt\":\"%s\",\"endAt\":\"%s\"}", timeSlot.get(0), timeSlot.get(1)))
              .post(getBaseUrl() + "/slots");
      if (response.then().extract().statusCode() == HttpStatus.CONFLICT.value()) {
        i--;
        slotsCreationSkipCount++;
        continue;
      }
      String slotId = response.then().extract().path("id");
      slotIds.add(UUID.fromString(slotId));
    }
    System.out.printf(
        "Slots skipping rate: %.1f%%\n",
        slotsCreationSkipCount * 100.0 / (slotsCreationSkipCount + TOTAL_SLOTS));
    return new ArrayList<>(slotIds);
  }

  private List<UUID> createMeetings(List<UUID> userIds, List<UUID> slotIds) {
    System.out.println("Creating meetings...");
    List<UUID> meetingIds = new LinkedList<>();
    for (int i = 0; i < TOTAL_MEETINGS; i++) {
      String slotId = getRandomSlot(slotIds);
      String title = generateRandomString();
      String participants = generateParticipants(userIds);
      Response response =
          given()
              .contentType(ContentType.JSON)
              .body(
                  String.format(
                      "{\"slotId\":\"%s\",\"title\":\"%s\",\"participants\":%s}",
                      slotId, title, participants))
              .post(getBaseUrl() + "/meetings");
      String meetingId = response.then().extract().path("id");
      meetingIds.add(UUID.fromString(meetingId));
    }
    return new ArrayList<>(meetingIds);
  }

  private int makeCalendarRequests() throws InterruptedException {
    System.out.println("Making calendar requests...");
    AtomicLong totalResponseTime = new AtomicLong(0);
    ExecutorService executor = Executors.newFixedThreadPool(CALENDAR_REQUESTS_THREADS);
    for (int t = 0; t < CALENDAR_REQUESTS_THREADS; t++) {
      executor.submit(
          () -> {
            for (int i = 0; i < CALENDAR_REQUESTS_PER_THREAD; i++) {
              Date date = new Date(getRandomMomentInMillis());
              String month = MONTH_FORMATTER.format(date);
              Response response = given().get(getBaseUrl() + "/calendars?month=" + month);
              totalResponseTime.addAndGet(response.getTime());
            }
          });
    }
    executor.shutdown();
    boolean isTerminatedProperly =
        executor.awaitTermination(CALENDAR_REQUESTS_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    if (isTerminatedProperly) {
      System.out.println("Calendar requests method reached timeout and has been terminated.");
      return (int)
          (totalResponseTime.get() / (CALENDAR_REQUESTS_THREADS * CALENDAR_REQUESTS_PER_THREAD));
    } else {
      return 0;
    }
  }

  private String generateRandomString() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
      int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char) randomLimitedInt);
    }
    return buffer.toString();
  }

  public String formatNanos(long nanos) {
    Duration duration = Duration.of(nanos, ChronoUnit.NANOS);
    return String.format(
        "%sm %ss %sms",
        duration.toMinutesPart(), duration.toSecondsPart(), duration.toMillisPart());
  }

  public List<String> generateTimeSlot() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(getRandomMomentInMillis());
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    long startSlotTime = calendar.getTimeInMillis();
    long endSlotTime = startSlotTime + ONE_HOUR_MILLIS;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    calendar.setTimeInMillis(startSlotTime);
    String startSlotTimeText = formatter.format(calendar.getTime());
    calendar.setTimeInMillis(endSlotTime);
    String endSlotTimeText = formatter.format(calendar.getTime());
    return List.of(startSlotTimeText, endSlotTimeText);
  }

  long getRandomMomentInMillis() {
    return new Date().getTime()
        - (long) (Math.random() * SLOT_TIMESPAN_DAYS * 24 * ONE_HOUR_MILLIS)
        + ONE_HOUR_MILLIS * 24 * 30;
  }

  String getRandomSlot(List<UUID> slotIds) {
    int randomIndex = (int) (Math.random() * slotIds.size());
    return slotIds.remove(randomIndex).toString();
  }

  String generateParticipants(List<UUID> userIds) {
    int participantsCount =
        (int) Math.round(Math.random() * (MEETING_SIZE_MAX - MEETING_SIZE_MIN)) + MEETING_SIZE_MIN;
    StringBuilder response = new StringBuilder("[");
    for (int i = 0; i < participantsCount; i++) {
      int randomIndex = (int) (Math.random() * userIds.size());
      UUID userId = userIds.get(randomIndex);
      response.append(String.format("{\"id\":\"%s\"},", userId));
    }
    response.replace(response.length() - 1, response.length(), "]");
    return response.toString();
  }
}
