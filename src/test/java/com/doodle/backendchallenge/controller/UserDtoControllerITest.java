package com.doodle.backendchallenge.controller;

import com.doodle.backendchallenge.controller.base.IntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

class UserDtoControllerITest extends IntegrationTest {

    @Test
    void createUser() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(

                        """
                        {
                          "name":"TestUser"
                        }
                        """
                )
                .post(getBaseUrl() + "/users");
        String responseId = response.then().extract().path("id");
        response
                .then()
                .assertThat()
                .statusCode(201)
                .body(containsString(String.format(
                        """
                        {
                          "id":"%s",
                          "name":"TestUser"
                        }
                        """, responseId).replace("\n", "").replace(" ", "")));
    }

    @Test
    void readUsers() {
        int initialSize = get(getBaseUrl() + "/users")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().body().path("totalSize");

        createUser("TestUser1");
        createUser("TestUser2");

        get(getBaseUrl() + "/users")
                .then()
                .assertThat()
                .statusCode(200)
                .body("totalSize", equalTo(initialSize + 2));
    }

    @Test
    void deleteUser() {
        String userId = createUser("TestUser3");
        MatcherAssert.assertThat(doesUserExists(userId), equalTo(true));

        delete(getBaseUrl() + "/users/" + userId)
                .then()
                .assertThat()
                .statusCode(200)
                .body(equalTo(""));

        MatcherAssert.assertThat(doesUserExists(userId), equalTo(false));
    }
}