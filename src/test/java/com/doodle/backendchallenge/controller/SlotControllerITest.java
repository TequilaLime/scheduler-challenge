package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.controller.base.IntegrationTest;
import com.doodle.backendchallenge.service.SlotService;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

class SlotControllerITest extends IntegrationTest {

  @Autowired private SlotService slotService;

  @Test
  void createSlot() {
    Response response =
        given()
            .contentType(ContentType.JSON)
            .body(
                    """
                    {
                      "startAt":"2021-09-29T21:00:00.000+02:00",
                      "endAt":"2021-09-29T22:00:00.000+02:00"
                    }
                    """)
            .post(getBaseUrl() + "/slots");
    String responseId = response.then().extract().path("id");
    response
        .then()
        .assertThat()
        .statusCode(201)
        .assertThat()
        .body(containsString(String.format(
                """
                {
                  "id":"%s",
                  "startAt":"2021-09-29T21:00:00+02:00",
                  "endAt":"2021-09-29T22:00:00+02:00"
                }
                """, responseId).replace("\n", "").replace(" ", "")));
  }

  @Test
  void createOverlappingSlot() {
    given()
        .contentType(ContentType.JSON)
        .body(
                """
                {
                "title":"Test Slot 1",
                "startAt":"2021-08-01T10:00:00.000+00:00",
                "endAt":"2021-08-01T11:00:00.000+00:00"
                }")
                """)
        .post(getBaseUrl() + "/slots")
        .then()
        .assertThat()
        .statusCode(201);
    given()
        .contentType(ContentType.JSON)
        .body(
                """
                {
                "title":"Test Slot 2",
                "startAt":"2021-08-01T09:50:00.000+00:00",
                "endAt":"2021-08-01T10:01:00.000+00:00"
                }
                """)
        .post(getBaseUrl() + "/slots")
        .then()
        .assertThat()
        .statusCode(409);
  }

  @Test
  void readSlots() {
    slotService.deleteAllSlots();

    String slotId1 = createSlot("2021-09-10T11:00:00+02:00", "2021-09-10T11:30:00+02:00");
    String slotId2 = createSlot("2021-09-11T11:00:00+02:00", "2021-09-11T11:30:00+02:00");

    get(getBaseUrl() + "/slots")
        .then()
        .assertThat()
        .statusCode(200)
        .assertThat()
        .body(containsString(String.format(
                """
                {
                  "items":
                  [
                    {"id":"%s","startAt":"2021-09-10T11:00:00+02:00","endAt":"2021-09-10T11:30:00+02:00"},
                    {"id":"%s","startAt":"2021-09-11T11:00:00+02:00","endAt":"2021-09-11T11:30:00+02:00"}
                  ],
                  "page":0,
                  "pageSize":2,
                  "totalSize":2
                }
                """, slotId1, slotId2).replace("\n", "").replace(" ", "")));
  }

  @Test
  void readSlot() {
    slotService.deleteAllSlots();

    String slotId1 = createSlot("2021-09-10T11:00:00+02:00", "2021-09-10T11:30:00+02:00");

    get(getBaseUrl() + "/slots/" + slotId1)
            .then()
            .assertThat()
            .statusCode(200)
            .assertThat()
            .body(containsString(String.format(
                    """
                    {
                      "id":"%s",
                      "startAt":"2021-09-10T11:00:00+02:00",
                      "endAt":"2021-09-10T11:30:00+02:00"
                    }
                    """, slotId1).replace("\n", "").replace(" ", "")));
  }

  @Test
  void deleteSlot() {
    String slotId = createSlot("2021-09-30T11:00:00+02:00", "2021-09-30T11:30:00+02:00");

    checkReadSlotStatusCode(slotId, HttpStatus.OK.value());

    delete(getBaseUrl() + "/slots/" + slotId)
        .then()
        .assertThat()
        .statusCode(200)
        .assertThat()
        .body(equalTo(""));

    checkReadSlotStatusCode(slotId, HttpStatus.NOT_FOUND.value());
  }
}
