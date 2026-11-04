package ru.ksu.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Test;
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


    // Тест №1
    @Test
    void shouldOpenGroup(Page page) {   // расширение Playwright для JUnit 5 автоматически создаёт BrowserContext и Page

        GroupPage groupPage = new GroupPage(page);  // Page Object для работы со страницей группы

        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );

        Allure.step("Проверить URL страницы", () ->
                assertThat(page).hasURL(GroupPage.URL)
        );

        Allure.step("Проверить отображение заголовка", () ->
                assertThat(groupPage.getGroupHeading()).isVisible()
        );
    }

    // Общий сценарий открытия группы, редактора темы - планируется вынести в ДЗ №5 (например, с помощью @BeforeEach)

    // Тест №2
    @Test
    void shouldOpenEditor(Page page) {

        GroupPage groupPage = new GroupPage(page);
        TopicEditorOverlay editor = new TopicEditorOverlay(page);   // конструктор только создаёт локатор, Playwright еще не ищет на странице

        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );

        Allure.step("Открыть редактор новой темы", () ->
                groupPage.openTopicEditor()
        );

        Allure.step("Проверить отображение поля ввода", () ->
                assertThat(editor.getTextPlaceholder()).isVisible()
        );

        Allure.step("Проверить доступность поля для ввода", () ->
                assertThat(editor.getTextField()).isEditable()
        );
    }

    // Тест №3
    @Test
    void fillTextInEditor(Page page) {

        GroupPage groupPage = new GroupPage(page);
        TopicEditorOverlay editor = new TopicEditorOverlay(page);

        String fillingText = "Начало";


        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );

        Allure.step("Открыть редактор новой темы", () ->
                groupPage.openTopicEditor()
        );

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
    void shouldClearTextInEditor(Page page) {

        GroupPage groupPage = new GroupPage(page);
        TopicEditorOverlay editor = new TopicEditorOverlay(page);

        String fillingText = "Тест №4";

        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );

        Allure.step("Открыть редактор новой темы", () ->
                groupPage.openTopicEditor()
        );

        Allure.step("Ввести текст в редактор", () ->
                editor.getTextField().fill(fillingText)
        );

        Allure.step("Очистить поле редактора", () ->
                editor.clearText()
        );

        Allure.step("Проверить, что поле редактора пустое", () ->
                assertThat(editor.getTextField()).isEmpty()
        );
    }

    // Тест №5
    @Test
    void publishingShouldBeDisabled(Page page) {
        GroupPage groupPage = new GroupPage(page);
        TopicEditorOverlay editor = new TopicEditorOverlay(page);

        Allure.step("Открыть страницу группы", () ->
                groupPage.open()
        );

        Allure.step("Открыть редактор новой темы", () ->
                groupPage.openTopicEditor()
        );

        Allure.step("Проверить недоступность публикования пустой темы", () ->
                assertThat(editor.getPublishButton()).isDisabled());
    }

    // -----
    // Планируемое развитие в ДЗ №5 - 1. проверка публикации поста; 2. проверка прикрепления фотографий
}
