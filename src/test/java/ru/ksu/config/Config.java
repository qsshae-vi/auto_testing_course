package ru.ksu.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public final class Config {
    public static final String BASE_URL = "https://simple-books-api.click";

    // общая спецфикация к API
    public static final RequestSpecification SPEC = new RequestSpecBuilder()
        .setBaseUri(BASE_URL)
        .build();

    private Config() { // закрытый конструктор
    }

    // Спецификация с авторизацией
    public static RequestSpecification auth(String token) {
        return new RequestSpecBuilder()
            .addRequestSpecification(SPEC)
            .addHeader("Authorization", "Bearer " + token)
            .build();
    }
}
 