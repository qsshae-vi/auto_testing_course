package ru.ksu.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class TopicEditorOverlay {

    private final Locator textPlaceholder;
    private final Locator textField;
    private final Locator publishButton;
    private final Locator closeButton;
    private final Locator deleteDraft;
    private final Locator editorDialog;


    public TopicEditorOverlay(Page page) {
        this.textPlaceholder = page.locator(".posting_itx-w").getByText("Введите текст");   // ограничиваем поиск областью страницы
        this.textField = page.locator("section[aria-label='Текст']").getByRole(AriaRole.TEXTBOX);
        this.publishButton = page.getByRole(
                AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Поделиться")).and(page.locator(".posting_submit"));
        this.closeButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Закрыть"));
        this.deleteDraft = page.getByRole(
                AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Удалить черновик")
        ).and(page.locator("button[data-action='clearDraft']"));
        this.editorDialog = page.getByRole(
                AriaRole.DIALOG
        ).and(page.locator("#mtLayer"));
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

    public void typeText(String text) {
        textField.click();  // фокус - полю
        textField.pressSequentially(text);  // иммитируем ввод прям с клавиатуры
    }

    public boolean isOpen() {
        return editorDialog.isVisible();
    }

    public void closeEditor() {
        closeButton.click();
    }

    public void clearDraft() {
        deleteDraft.click();
    }
}

