package ru.ksu.clients;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.ksu.config.Config;

public class OrderClient {
    private final String token;

    public OrderClient(String token) {
        this.token = token;
    }

    public Response getOrdersWithoutAuth() {    // для негативного теста 
        return given(Config.SPEC)
            .when()
                .get("/orders");
    }

    public Response getOrders() {
        return given(Config.auth(token))
            .when()
                .get("/orders");
    }

    public Response getOrder(String orderId) {
        return given(Config.auth(token))
            .pathParam("id", orderId)
            .when()
                .get("/orders/{id}");
    }

    public Response submitOrder(int bookId, String clientName) {
        String body = """
            {
                "bookId": %d,
                "customerName": "%s"
            }
            """.formatted(bookId, clientName);

        return given(Config.auth(token))
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post("/orders");
    }

    public Response submitOrderWithoutBookId(String clientName) {
        String body = """
            {
                "customerName": "%s"
            }
            """.formatted(clientName);

        return given(Config.auth(token))
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post("/orders");
    }

    public Response deleteOrder(String orderId) {
        return given(Config.auth(token))
            .pathParam("id", orderId)    
            .when()
                .delete("/orders/{id}");
    }

    public Response submitOrderWithInvalidName(int clientName, int bookId) {    // not used
    String body = """
        {
            "customerName": %d,
            "bookId": %d
        }
        """.formatted(clientName, bookId);

    return given(Config.auth(token))
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/orders");
    }
}
