package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.controller.base.IntegrationTest;
import com.doodle.backendchallenge.service.MeetingService;
import com.doodle.backendchallenge.service.SlotService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class MeetingDtoControllerITest extends IntegrationTest {

  @Autowired
  SlotService slotService;
  @Autowired
  MeetingService meetingService;

  @Test
  void createMeetingWithoutParticipants() {
    slotService.deleteAllSlots();
    meetingService.deleteAllMeetings();

    String slotId = createSlot("2021-01-01T10:00:00+02:00", "2021-01-01T11:00:00+02:00");

    given()
        .contentType(ContentType.JSON)
        .body(String.format(
                """
                {
                  "slotId":"%s",
                  "title":"TestMeeting1"
                }
                """, slotId))
        .post(getBaseUrl() + "/meetings")
        .then()
        .assertThat()
        .statusCode(201)
        .assertThat()
        .body(containsString(String.format(
                """
                {
                  "id":"%s",
                  "title":"Test Meeting 1",
                  "startAt":"2021-01-01T09:00:00+01:00",
                  "endAt":"2021-01-01T10:00:00+01:00",
                  "participants":[]
                }
                """, slotId).replace("\n", "").replace(" ", "")));
  }

  @Transactional
  @Test
  void createMeetingWithParticipants() {
    String slotId = createSlot("2021-01-01T11:00:00+02:00", "2021-01-01T12:00:00+02:00");
    String userAId = createUser("TestUser1");
    String userBId = createUser("TestUser2");
    String userCId = createUser("TestUser3");

    given()
        .contentType(ContentType.JSON)
        .body(String.format(
                """
                {
                  "slotId":"%s",
                  "title":"%s",
                  "participants":[
                    {"id":"%s"},
                    {"id":"%s"},
                    {"id":"%s"}
                  ]
                }
                """, slotId, "TestMeeting2", userAId, userBId, userCId))
        .post(getBaseUrl() + "/meetings")
        .then()
        .assertThat()
        .statusCode(201)
        .assertThat()
        .body(containsString(String.format(
                """
                {
                  "id":"%s",
                  "title":"TestMeeting2",
                  "startAt":"2021-01-01T10:00:00+01:00",
                  "endAt":"2021-01-01T11:00:00+01:00",
                  "participants":[
                    {"id":"%s","name":"TestUser1"},
                    {"id":"%s","name":"TestUser2"},
                    {"id":"%s","name":"TestUser3"}
                  ]
                }
                """, slotId, userAId, userBId, userCId).replace("\n", "").replace(" ", "")));
  }

  @Test
  void createMeetingAndCheckSlot() {
    String slotId = createSlot("2021-01-01T12:00:00.000+02:00", "2021-01-01T13:00:00.000+02:00");
    String meetingId = createMeeting(slotId, "TestMeeting3", List.of());

    get(getBaseUrl() + "/slots/" + slotId).then().assertThat().statusCode(404);

    get(getBaseUrl() + "/meetings/" + meetingId)
        .then()
        .assertThat()
        .statusCode(200)
        .assertThat()
        .body(not(emptyOrNullString()));
  }

  @Test
  void deleteMeeting() {
    String slotId = createSlot("2021-01-04T10:00:00.000+02:00", "2021-01-04T11:00:00.000+02:00");
    String meetingId = createMeeting(slotId, "TestMeeting4", List.of());

    delete(getBaseUrl() + "/meetings/" + meetingId)
        .then()
        .assertThat()
        .statusCode(200)
        .assertThat()
        .body(equalTo(""));

    get(getBaseUrl() + "/meetings/" + meetingId).then().assertThat().statusCode(404);

    get(getBaseUrl() + "/slots/" + slotId).then().assertThat().statusCode(200);
  }
}
