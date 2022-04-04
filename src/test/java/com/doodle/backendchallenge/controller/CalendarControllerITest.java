package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.controller.base.IntegrationTest;
import com.doodle.backendchallenge.service.MeetingService;
import com.doodle.backendchallenge.service.SlotService;
import com.doodle.backendchallenge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

class CalendarControllerITest extends IntegrationTest {

  @Autowired public UserService userService;
  @Autowired public SlotService slotService;
  @Autowired public MeetingService meetingService;

  @BeforeEach
  public void setup() {
    meetingService.deleteAllMeetings();
    slotService.deleteAllSlots();
    userService.deleteAllUsers();
  }

  @Test
  void fetchCalendarMonth() {
    userService.deleteAllUsers();
    slotService.deleteAllSlots();
    meetingService.deleteAllMeetings();

    String userId1 = createUser("User1");
    String userId2 = createUser("User2");
    String userId3 = createUser("User3");
    String userId4 = createUser("User4");
    String userId5 = createUser("User5");
    String userId6 = createUser("User6");
    String userId7 = createUser("User7");

    String slotId1 = createSlot("2021-01-01T10:00:00+02:00", "2021-01-01T11:00:00+02:00");
    String slotId2 = createSlot("2021-01-01T11:00:00+02:00", "2021-01-01T12:00:00+02:00");
    String slotId3 = createSlot("2021-01-01T12:00:00+02:00", "2021-01-01T13:00:00+02:00");
    String slotId4 = createSlot("2021-01-02T10:00:00+02:00", "2021-01-02T11:00:00+02:00");
    String slotId5 = createSlot("2021-01-02T11:00:00+02:00", "2021-01-02T12:00:00+02:00");
    String slotId6 = createSlot("2021-01-02T12:00:00+02:00", "2021-01-02T13:00:00+02:00");
    String slotId7 = createSlot("2021-01-03T10:00:00+02:00", "2021-01-03T11:00:00+02:00");
    String slotId8 = createSlot("2021-01-03T11:00:00+02:00", "2021-01-03T12:00:00+02:00");
    String slotId9 = createSlot("2021-01-03T12:00:00+02:00", "2021-01-03T13:00:00+02:00");

    String slotId10 = createSlot("2020-12-01T10:00:00+02:00", "2020-12-01T11:00:00+02:00");
    createSlot("2020-12-01T11:00:00+02:00", "2020-12-01T12:00:00+02:00");
    createSlot("2020-12-01T12:00:00+02:00", "2020-12-01T13:00:00+02:00");

    String slotId20 = createSlot("2021-02-01T10:00:00+02:00", "2021-02-01T11:00:00+02:00");
    String slotId21 = createSlot("2021-02-01T11:00:00+02:00", "2021-02-01T12:00:00+02:00");
    createSlot("2021-02-01T12:00:00+02:00", "2021-02-01T13:00:00+02:00");

    createMeeting(slotId1, "Meeting1", List.of());
    createMeeting(slotId5, "Meeting2", List.of());
    createMeeting(slotId9, "Meeting3", List.of());
    createMeeting(slotId10, "Meeting4", List.of());
    createMeeting(slotId20, "Meeting5", List.of());
    createMeeting(slotId21, "Meeting6", List.of());

    given()
        .get(getBaseUrl() + "/calendars?month=2021-01")
        .then()
        .assertThat()
        .statusCode(200)
        .assertThat()
        .body(containsString(String.format(
                """
                {
                  "days":{
                    "1":{
                      "slots":[
                        {
                          "id":"%s",
                          "startAt":"2021-01-01T10:00:00+01:00","endAt":"2021-01-01T11:00:00+01:00"
                        },
                        {
                          "id":"%s",
                          "startAt":"2021-01-01T11:00:00+01:00",
                          "endAt":"2021-01-01T12:00:00+01:00"
                        }
                      ],
                      "meetings":[
                        {
                          "id":"%s",
                          "title":"Meeting1",
                          "startAt":"2021-01-01T09:00:00+01:00",
                          "endAt":"2021-01-01T10:00:00+01:00",
                          "participants":[]
                        }
                      ]
                    },
                    "2":{
                      "slots":[
                        {"id":"%s","startAt":"2021-01-02T09:00:00+01:00","endAt":"2021-01-02T10:00:00+01:00"},
                        {"id":"%s","startAt":"2021-01-02T11:00:00+01:00","endAt":"2021-01-02T12:00:00+01:00"}
                      ],
                      "meetings":[
                        {"id":"%s","title":"Meeting2","startAt":"2021-01-02T10:00:00+01:00","endAt":"2021-01-02T11:00:00+01:00","participants":[]}
                      ]
                    },
                    "3":{
                      "slots":[
                        {"id":"%s","startAt":"2021-01-03T09:00:00+01:00","endAt":"2021-01-03T10:00:00+01:00"},
                        {"id":"%s","startAt":"2021-01-03T10:00:00+01:00","endAt":"2021-01-03T11:00:00+01:00"}
                      ],
                      "meetings":[
                        {"id":"%s","title":"Meeting3","startAt":"2021-01-03T11:00:00+01:00","endAt":"2021-01-03T12:00:00+01:00","participants":[]}
                      ]
                    }
                  },
                  "slotsBefore":2,
                  "slotsAfter":1,
                  "meetingsBefore":1,
                  "meetingsAfter":2
                }
                """, slotId2, slotId3, slotId1, slotId4, slotId6, slotId5, slotId7, slotId8, slotId9).replace("\n", "").replace(" ", "")));
  }
}
