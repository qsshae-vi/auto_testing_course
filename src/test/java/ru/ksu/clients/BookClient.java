package ru.ksu.clients;

import ru.ksu.config.Config;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BookClient {

    public Response getBook(int id) {
        return given(Config.SPEC)
            .pathParam("id", id)
        .when()
            .get("/books/{id}");
    }

    public Response getBooksWithLimit(int limit) {
        return given(Config.SPEC)
                .queryParam("limit", limit)
            .when()
                .get("/books");
    }

    public Response getBooksByType(String type) {
    return given(Config.SPEC)
            .queryParam("type", type)
        .when()
            .get("/books");
    }
}
