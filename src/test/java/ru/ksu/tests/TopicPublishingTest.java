package ru.ksu.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import ru.ksu.components.DraftComponent;
import ru.ksu.components.PostComponent;
import ru.ksu.components.TopicEditorOverlay;
import ru.ksu.pages.GroupPage;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(ru.ksu.tests.GroupPostTest.TestOptions.class)
// используем определённую в первом (по порядку создания) классе конфигурацию
public class TopicPublishingTest {  // Можно будет расширить в сторону тестирования публикации вложений

    private GroupPage groupPage;
    private TopicEditorOverlay editor;
    private PostComponent post;
    private DraftComponent draft;

    @BeforeEach
    void openGroup(Page page) {
        groupPage = new GroupPage(page);

        editor = new TopicEditorOverlay(page);
        draft = groupPage.getDraft();

        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );

        Allure.step("Очищаем черновик поста, если не пустой",
                () -> clearDraftIfNeeded());

        Allure.step("Открыть редактор новой темы", () ->
                groupPage.openTopicEditor()
        );
    }

    @AfterEach
    void cleanUp() {
        try {
            if (post != null && post.getRoot().count() == 1) {
                Allure.step("Удалить тестовую публикацию",
                        () -> post.deletePost());
            }
        } finally {
            DraftComponent draft = groupPage.getDraft();

            Allure.step("Очищаем черновик поста, если не пустой",
                    () -> clearDraftIfNeeded());
        }
    }

    @Test
    @Tag("smoke")
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    @DisplayName("Публикация текстовой темы в группе")
    void shouldPublishTextTopic(Page page) {

        String topicText = "Автотест " + UUID.randomUUID();     // так как необходима проверка появления поста в ленте

        Allure.step("Ввести текст публикации", () ->
                editor.typeText(topicText)
        );

        Allure.step("Проверить доступность кнопки публикации", () ->
                assertThat(editor.getPublishButton()).isEnabled()
        );

        Allure.step("Опубликовать тему",
                () -> editor.publish());

        Allure.step("Обновить страницу группы", () ->
                groupPage.open()
        );

        // конкретный опубликованный пост
        post = groupPage.getPost(topicText);

        Allure.step("Проверить появление опубликованного поста", () ->
                assertThat(post.getRoot()).hasCount(1)
        );

        Allure.step("Проверить отображение опубликованного поста", () ->
                assertThat(post.getRoot()).isVisible()
        );
    }

    @Test
    @Disabled("Вспомогательный тест очистки черновика")
    void shouldDeleteDraft(Page page) {
        Allure.step("Удалить черновик",
                () -> clearDraftIfNeeded());
    }

    private void clearDraftIfNeeded() {
        if (editor.isOpen()) {  // Редактор открыт
            Allure.step("Закрыть редактор", () -> editor.closeEditor());
        }

        if (!draft.isExists()) {
            Allure.step("Черновик отсутствует - очистка не требуется");
            return;
        }

        Allure.step("Открываем имеющийся черновик", () -> draft.open());
        Allure.step("Удалить сохранённый черновик", () -> editor.clearDraft());
        Allure.step("Закрыть редактор", () -> editor.closeEditor());
    }
}
