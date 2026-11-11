package ru.ksu.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.ksu.components.TopicEditorOverlay;
import ru.ksu.pages.GroupPage;

import java.nio.file.Paths;

@UsePlaywright(GroupPostTest.TestOptions.class)

public class GroupPostTest {

    public static class TestOptions implements OptionsFactory {

        @Override
        public Options getOptions() {
            return new Options()
                    .setHeadless(true)
                    .setContextOptions(
                            new Browser.NewContextOptions()
                                    .setBaseURL("https://ok.ru")
                                    .setStorageStatePath(
                                            Paths.get("playwright/.auth/user.json")
                                    )
                    );
        }
    }

    private Page page;
    private GroupPage groupPage;
    private TopicEditorOverlay editor;

    // Подготовка для всех(каждого) тестов
    @BeforeEach
    void openGroup(Page page) {
        this.page = page;   // задаём  в качестве поля класса - параметр
        groupPage = new GroupPage(page);

        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );
    }

    // Тест №1
    @Test
    @Tag("smoke")   // относится к базовой, критической проверке
    @DisplayName("Открытие страницы группы и отображение заголовка")
    void shouldOpenGroup() {

        Allure.step("Проверить страницу группы", () ->
                assertAll(      // группируем утверждения
                        "Состояние страницы группы",

                        () -> Allure.step("Проверить URL страницы", () ->
                                assertThat(page).hasURL(GroupPage.URL)
                        ),

                        () -> Allure.step("Проверить отображение заголовка", () ->
                                assertThat(groupPage.getGroupHeading()).isVisible()
                        )
                )
        );
    }

    @Nested
    @DisplayName("Редактор создания новой темы")
    class EditorTests {
        //Подготовка только для тестов редактора (вложенных)
        @BeforeEach
        void openEditor() {
            editor = new TopicEditorOverlay(page);

            Allure.step("Открыть редактор новой темы", () ->
                    groupPage.openTopicEditor()
            );
        }

        @AfterEach
        void clearDraft() {
            if (editor != null) {
                Allure.step("Очищаем черновик поста, если не пустой", () ->
                        editor.clearDraftIfNeeded());
            }
        }

        // Тест №2
        @Test
        @DisplayName("Отображение и доступность поля ввода текста")
        void shouldOpenEditor() {

            Allure.step("Проверить отображение поля ввода", () ->
                    assertThat(editor.getTextPlaceholder()).isVisible()
            );

            Allure.step("Проверить доступность поля для ввода", () ->
                    assertThat(editor.getTextField()).isEditable()
            );
        }

        // Тест №3
        @ParameterizedTest(name = "Ввод текста: {0}")
        @ValueSource(strings = {
                "Начало",
                "Текст с цифрами 123",
                "Текст с символами !?%^&#@+-"
        })
        @DisplayName("Ввод текста в поле редактора")
        void fillTextInEditor(String fillingText) {

            try {
                Allure.step("Ввести текст в редактор", () ->
                        editor.fillText(fillingText)
                );

                Allure.step("Проверить введённый текст", () ->
                        assertThat(editor.getTextField()).hasText(fillingText)
                );
            } finally {
                Allure.step("Очистить текст редактора", () ->
                        editor.clearText()
                );
            }
        }

        // Тест №4
        @Test
        @DisplayName("Очистка введенного текста в редакторе")
        void shouldClearTextInEditor() {

            String fillingText = "Тест №4";

            Allure.step("Ввести текст в редактор", () ->
                    editor.fillText(fillingText));

            Allure.step("Очистить поле редактора", () ->
                    editor.clearText()
            );

            Allure.step("Проверить, что поле редактора пустое", () ->
                    assertThat(editor.getTextField()).isEmpty()
            );
        }

        // Тест №5
        @Test
        @Tag("negative")
        @DisplayName("Кнопка публикации недоступна при путом поле ввода")
        void publishingShouldBeDisabled() {

            Allure.step("Проверить недоступность публикования пустой темы", () ->
                    assertThat(editor.getPublishButton()).isDisabled());
        }
    }
}
