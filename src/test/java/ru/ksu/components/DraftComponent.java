package ru.ksu.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class DraftComponent {
    private final Locator draft;

    public DraftComponent(Page page) {
        this.draft = page.getByLabel("Продолжить тему...");
    }

    public boolean isExists() {
        return draft.count() > 0;
    }

    public void open() {
        draft.click();
    }
}
