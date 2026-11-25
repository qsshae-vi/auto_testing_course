package ru.ksu.model;

import java.util.UUID;

// VALUE OBJECT - контейнер тестовых данных, который умеет сам себя генерировать и знает правила предметной области
public record TopicText(String text) {
    public TopicText {
        if (text.isBlank()) {
            throw new IllegalArgumentException("Текст темы не может быть пустым");
        }
    }

    // шаблонная генерация
    public static TopicText random() {
        return new TopicText("Автотест " + UUID.randomUUID());
    }
}