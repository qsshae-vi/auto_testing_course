package ru.ksu.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import ru.ksu.components.TopicEditorOverlay;
import ru.ksu.pages.GroupPage;

import java.util.concurrent.TimeUnit;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(ru.ksu.tests.GroupPostTest.TestOptions.class)    // используем определённую в первом (по порядку создания) классе конфигурацию
public class TopicPublishingTest {
    TopicEditorOverlay editor;

    @AfterEach
    void clearDraft() {
        if (editor != null) {
            Allure.step("Очищаем черновик поста, если не пустой", () ->
                    editor.clearDraftIfNeeded());
        }
    }

    @Test
    @Tag("smoke")
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    @DisplayName("Публикация текстовой темы в группе")
    void shouldPublishTextTopic(Page page) {

        GroupPage groupPage = new GroupPage(page);
        editor = new TopicEditorOverlay(page);

        String topicText = "Текст для поста с последующей публикацей";

        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );

        Allure.step("Открыть редактор новой темы", () ->
                groupPage.openTopicEditor()
        );

        Allure.step("Ввести текст публикации", () ->
                editor.typeText(topicText)
        );

        Allure.step("Проверить доступность кнопки публикации", () ->
                assertThat(editor.getPublishButton()).isEnabled()
        );

        Allure.step("Опубликовать тему",
                () -> editor.publish());

        Allure.step("Проверить отображение опубликованной темы", () ->
                assertThat(groupPage.getPostByText(topicText)).isVisible()
        );
    }
}
