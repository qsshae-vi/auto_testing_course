package ru.ksu.clients;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import io.restassured.http.ContentType;
import ru.ksu.config.Config;

public class AuthClient {
        public String getToken() {
                String email = "api-" + UUID.randomUUID() + "@example.com";
                String credentials = """
                {
                        "clientName": "AutoTestingCourse",
                        "clientEmail": "%s"
                }
                """.formatted(email);

                return given(Config.SPEC)
                        .contentType(ContentType.JSON)
                        .body(credentials)
                        .when()
                                .post("/api-clients/")
                        .then()
                                .statusCode(201)
                        .extract()
                                .path("accessToken");
        }
}
