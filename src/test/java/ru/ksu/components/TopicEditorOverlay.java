package ru.ksu.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class TopicEditorOverlay { // в ДЗ№6 можно будет использовать паттерн Component

    private final Locator textPlaceholder;
    private final Locator textField;
    private final Locator publishButton;


    public TopicEditorOverlay(Page page) {
        this.textPlaceholder = page.locator(".posting_itx-w").getByText("Введите текст");   // ограничиваем поиск областью страницы
        this.textField = page.locator(".posting_itx[contenteditable='true']"); // поиск по CSS-классу и атрибуту contenteditable
        this.publishButton = page.getByRole(
                AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Поделиться")).and(page.locator(".posting_submit"));
    }

    public Locator getTextPlaceholder() {
        return textPlaceholder;
    }

    public Locator getTextField() {
        return textField;
    }

    public Locator getPublishButton() {
        return publishButton;
    }

    public void fillText(String text) {
        textField.fill(text);
    }

    public void clearText() {
        textField.clear();
    }

    public void publish() {
        publishButton.click();
    }

    public void clearDraftIfNeeded() {
        if (!textField.isVisible()) {
            return;
        }

        if (!textField.innerText().isBlank()) {     // возвращает true, если строка имеет нулевую длину или содержит только пробелы, табуляции и переносы
            textField.clear();
        }
    }

    public void typeText(String text) {
        textField.click();  // фокус - полю
        textField.pressSequentially(text);  // иммитируем ввод прям с клавиатуры
    }

}

