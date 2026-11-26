package ru.ksu.data;

import java.util.stream.Stream;

public final class TopicTextDataProvider {  // DATA PROVIDER
    private TopicTextDataProvider() {
    }

    public static Stream<String> topicTexts() {
        return Stream.of(
                "Начало",
                "Текст с цифрами 123",
                "Текст с символами !?%^&#@+-"
        );
    }
}