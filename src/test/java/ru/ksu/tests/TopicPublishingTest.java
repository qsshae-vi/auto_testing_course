package ru.ksu.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import ru.ksu.flows.DraftFlow;
import ru.ksu.model.TopicText;
import ru.ksu.components.PostComponent;
import ru.ksu.components.TopicEditorOverlay;
import ru.ksu.pages.GroupPage;

import java.util.concurrent.TimeUnit;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(ru.ksu.tests.GroupPostTest.TestOptions.class)
// используем определённую в первом (по порядку создания) классе конфигурацию
public class TopicPublishingTest {

    private GroupPage groupPage;
    private TopicEditorOverlay editor;
    private PostComponent post;

    private DraftFlow draftFlow;

    @BeforeEach
    void openGroup(Page page) {
        groupPage = new GroupPage(page);

        editor = new TopicEditorOverlay(page);

        draftFlow = new DraftFlow(page);


        Allure.step("Открыть страницу группы", () ->
                groupPage.get()
        );

        Allure.step("Очистить черновик, если не пустой",
                () -> draftFlow.clearIfNeeded());

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
            Allure.step("Очистить черновик, если не пустой",
                    () -> draftFlow.clearIfNeeded());
        }
    }

    @Test
    @Tag("smoke")
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    @DisplayName("Публикация текстовой темы в группе")
    void shouldPublishTextTopic(Page page) {

        //String topicText = "Автотест " + UUID.randomUUID();     // так как необходима проверка появления поста в ленте
        TopicText topic = TopicText.random();   // VALUE OBJECT + фабричный метод

        Allure.step("Ввести текст публикации", () ->
                editor.typeText(topic.text())
        );

        Allure.step("Проверить доступность кнопки публикации", () ->
                assertThat(editor.getPublishButton()).isEnabled()
        );

        Allure.step("Опубликовать тему", () ->
                editor.publish()
        );

        Allure.step("Обновить страницу группы", () -> groupPage.reload());

        // конкретный опубликованный пост
        post = groupPage.getPost(topic.text());

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
        Allure.step("Очистить черновик, если не пустой",
                () -> draftFlow.clearIfNeeded());
    }
}
