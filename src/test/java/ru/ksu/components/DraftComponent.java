package ru.ksu.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class DraftComponent {
    private final Locator draft;
    private final Page page;

    public DraftComponent(Page page) {
        this.page = page;
        this.draft = page.getByLabel("Продолжить тему...");
    }

    public boolean isExists() {
        return draft.count() > 0;
    }

    public TopicEditorOverlay open() {
        draft.click();
        return new TopicEditorOverlay(page);    // для Chain of invocations
    }
}
