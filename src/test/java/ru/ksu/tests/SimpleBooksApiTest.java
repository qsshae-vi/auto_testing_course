package ru.ksu.tests;

import io.qameta.allure.Allure;

// ТЗ: 5-7 тестов (positive/negative, границы, валидация)
// Chosen REST API - Simple Books API

import io.restassured.response.Response;
import ru.ksu.clients.AuthClient;
import ru.ksu.clients.BookClient;
import ru.ksu.clients.OrderClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.params.ParameterizedTest;

public class SimpleBooksApiTest {

    private final BookClient bookClient = new BookClient();
    private static OrderClient orderClient;

    @BeforeAll
    static void setUp() {
        String token = new AuthClient().getToken();
        orderClient = new OrderClient(token);
    }

    // ----- POSITIVE ------

    // 1. Getting existing books
    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 5 })
    void shouldReturnExistingBook(int bookId) {

        Response response = bookClient.getBook(bookId);

        checkResponse(response, () -> {
            assertEquals(200, response.statusCode());
            assertEquals("application/json", response.contentType().split(";")[0]); // проверяем, что вернули json
            assertEquals(bookId, response.jsonPath().getInt("id"), "Returned book ID does not match requested ID");

            String type = response.jsonPath().getString("type");

            assertTrue(
                    "fiction".equals(type) || "non-fiction".equals(type), // (type) для защиты от NullPointerException
                    "Book type must be fiction or non-fiction");
        });
    }

    // @Test
    // void shouldReturnOrdersWithAuthorization() {
    // Response response = orderClient.getOrders();

    // // response.then().log().body();

    // assertEquals(200, response.statusCode(), "Expected 200 OK when getting orders
    // with authorization");
    // }

    // 2. Submitting order
    @Test
    void shouldSubmitOrder() {
        int bookId = 1;
        String clientName = "Kseniya";

        // Создаём заказ
        Response response = orderClient.submitOrder(bookId, clientName);

        // response.then().log().body();

        checkResponse(response, () -> {
            assertEquals(201, response.statusCode(), "Expected 201 Created for submitted order");
            assertEquals("application/json", response.contentType().split(";")[0]); // проверяем, что вернули json
            assertTrue(response.jsonPath().getBoolean("created"), "Order creation must be confirmed");
        });

        String orderId = response.jsonPath().getString("orderId");
        assertNotNull(orderId, "Expected not null order ID for submitted order");

        // Проверяем, что заказ создан
        Response createdOrder = orderClient.getOrder(orderId);

        checkResponse(createdOrder, () -> {
            assertEquals(200, createdOrder.statusCode(),
                    "Expected 200 OK when getting created order");

            assertEquals("application/json", createdOrder.contentType().split(";")[0],
                    "Expected JSON response when getting created order");

            assertEquals(bookId, createdOrder.jsonPath().getInt("bookId"),
                    "Saved book ID does not match submitted book ID");

            assertEquals(clientName, createdOrder.jsonPath().getString("customerName"),
                    "Saved customer name does not match submitted name");
        });
    }

    // 3. Get filtered books (by type)
    @Test
    void shouldFilterBooksByType() {
        String type = "fiction";

        Response response = bookClient.getBooksByType(type);

        checkResponse(response, () -> {
            assertEquals(200, response.statusCode(),
                    "Expected 200 OK when filtering books");

            assertEquals("application/json", response.contentType().split(";")[0]);

            List<String> types = response.jsonPath().getList("type");

            assertTrue(!(types.isEmpty()),
                    "Expected at least one book");

            for (String bookType : types) {
                assertEquals(type, bookType,
                        "Book type does not match the requested type");
            }
        });
    }

    // ----- NEGATIVE ------
    // 4. Trying to get /orders without autorization
    @Test
    void shouldRestrictGettingOrders() {
        Response response = orderClient.getOrdersWithoutAuth();

        // response.then().log().all();

        checkResponse(response, () -> {
            assertEquals(401, response.statusCode(),
                    "Expected 401 Unauthorized for request without authentication");

            assertEquals("application/json", response.contentType().split(";")[0]);

            // проверяем поле ответа
            assertEquals("Missing Authorization header.",
                    response.jsonPath().getString("error"));
        });
    }

    // 5. Deleting non-existent order
    // @Test
    void shouldRejectDeletingNonExistentOrder() {

        String orderId = "non-existent-order-999999";

        Response response = orderClient.deleteOrder(orderId);

        // System.out.println(response.asPrettyString());

        checkResponse(response, () -> {
            assertEquals(404, response.statusCode(), "Expected 404 the targeted order does not exist");
        });
    }

    // ----- BOUNDARY -----
    // 6. Getting book with the limit near maximum
    @ParameterizedTest
    @CsvSource({ // comma-separated values
            "20, 200", // 1 - limit, 2 - expectedStatusCode
            "21, 400"
    })
    void shouldConfirmBooksLimit(int limit, int expectedStatus) {
        Response response = bookClient.getBooksWithLimit(limit);

        checkResponse(response, () -> {
            assertEquals(expectedStatus, response.statusCode(),
                    "Unexpected status for limit = " + limit);
        });
    }

    // ----- VALIDATION -----
    // 7. Submitting book order without book ID
    @Test
    void shouldRestrictSubbmitingOrder() {
        String customerName = "Maksim";

        Response response = orderClient.submitOrderWithoutBookId(customerName);

        // response.then().log().all();

        checkResponse(response, () -> {
            assertEquals(400, response.statusCode(),
                    "Expected 400 for missing book ID");

            assertEquals("application/json", response.contentType().split(";")[0]);

            // проверяем поле ответа
            assertEquals("Invalid or missing bookId.",
                    response.jsonPath().getString("error"));
        });
    }

    // Allure
    private static void checkResponse(Response response, Runnable assertions) {
        try {
            assertions.run(); // вызываем срабатывание проверок
        } catch (AssertionError error) { // срабатывает при неуспешном утверждении JUnit
            Allure.addAttachment( // прикладываем ответ сервера
                    "Server response on failure",
                    "application/json",
                    response.asPrettyString());
            throw error;
        }
    }

    // -----

    // Обнаруженный дефект: POST /orders, несмотря на требование String, принимает
    // числовое значение customerName,
    // возвращает 201 Created и сохраняет поле с типом Number

    // @Test
    // void shouldRestrictSubbmitingOrder2() {
    // int customerName = 123;
    // int bookId = 5;

    // Response response = orderClient.submitOrderWithInvalidName(customerName,
    // bookId);

    // String orderId = response.jsonPath().getString("orderId");

    // Response createdOrder = orderClient.getOrder(orderId);

    // System.out.println(createdOrder.asPrettyString());

    // response.then().log().all();

    // checkResponse(response, () -> {
    // assertEquals(400, response.statusCode(),
    // "Expected 400 for invalid client's name");
    // });
    // }
}