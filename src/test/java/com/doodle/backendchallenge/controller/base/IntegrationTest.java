package com.doodle.backendchallenge.controller.base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = IntegrationTest.DockerPostgreDataSourceInitializer.class)
@ExtendWith(SpringExtension.class)
@Testcontainers
public abstract class IntegrationTest {
  public static PostgreSQLContainer<?> postgreDBContainer =
      new PostgreSQLContainer<>("postgres:13")
          .withDatabaseName("backend-challenge")
          .withUsername("doodle")
          .withPassword("doodle");

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
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  public String createUser(String name) {
    return given()
        .contentType(ContentType.JSON)
        .body(String.format("{\"name\":\"%s\"}", name))
        .post(getBaseUrl() + "/users")
        .then()
        .assertThat()
        .statusCode(201)
        .extract()
        .path("id");
  }

  public String createSlot(String startAt, String endAt) {
    return given()
        .contentType(ContentType.JSON)
        .body(String.format("{\"startAt\":\"%s\",\"endAt\":\"%s\"}", startAt, endAt))
        .post(getBaseUrl() + "/slots")
        .then()
        .assertThat()
        .statusCode(201)
        .extract()
        .path("id");
  }

  public String createMeeting(String slotId, String title, List<String> participants) {
    return given()
        .contentType(ContentType.JSON)
        .body(
            String.format(
                "{\"slotId\":\"%s\",\"title\":\"%s\",\"participants\":%s}",
                slotId, title, generateParticipants(participants)))
        .post(getBaseUrl() + "/meetings")
        .then()
        .assertThat()
        .statusCode(201)
        .extract()
        .path("id");
  }

  public void checkReadSlotStatusCode(String slotId, int expectedStatusCode) {
    get(getBaseUrl() + "/slots/" + slotId)
        .then()
        .assertThat()
        .statusCode(expectedStatusCode)
        .assertThat()
        .body(not(emptyString()));
  }

  private String generateParticipants(List<String> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return "[]";
    }

    StringBuilder response = new StringBuilder("[");
    for (String userId : userIds) {
      response.append(String.format("{\"id\":\"%s\"},", userId));
    }
    response.replace(response.length() - 1, response.length(), "]");
    return response.toString();
  }

  public boolean doesUserExists(String userId) {
    int statusCode =
        given()
            .contentType(ContentType.JSON)
            .get(getBaseUrl() + "/users/" + userId)
            .then()
            .extract()
            .statusCode();
    if (statusCode == 200) {
      return true;
    } else if (statusCode == 404) {
      return false;
    } else {
      throw new IllegalStateException("Status code should to be 200 or 404");
    }
  }

  public boolean doesSlotExists(String slotId) {
    int statusCode =
        given()
            .contentType(ContentType.JSON)
            .get(getBaseUrl() + "/slots/" + slotId)
            .then()
            .extract()
            .statusCode();
    if (statusCode == 200) {
      return true;
    } else if (statusCode == 404) {
      return false;
    } else {
      throw new IllegalStateException("Status code should to be 200 or 404");
    }
  }

  public boolean doesMeetingExists(String meetingId) {
    int statusCode =
        given()
            .contentType(ContentType.JSON)
            .get(getBaseUrl() + "/meetings/" + meetingId)
            .then()
            .extract()
            .statusCode();
    if (statusCode == 200) {
      return true;
    } else if (statusCode == 404) {
      return false;
    } else {
      throw new IllegalStateException("Status code should to be 200 or 404");
    }
  }
}
