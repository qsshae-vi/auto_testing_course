package ru.ksu.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;

public final class PostComponent {

    private final Locator root;
    private final Locator menuTrigger;
    private final Locator actionsMenu;

    public PostComponent(Locator root) {    // root - контейнер конкретного поста
        this.root = root;
        this.menuTrigger = root.getByRole(
                AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Меню")  // Кнопка с тремя точками
        );
        this.actionsMenu = root.locator(
                "[data-module='ShortcutMenu']"
        );
    }

    public Locator getRoot() {
        return root;
    }

    public void deletePost() {
        menuTrigger.hover();

        actionsMenu.getByText(
                "Удалить тему",
                new Locator.GetByTextOptions().setExact(true)
        ).click();

        root.getByRole(
                AriaRole.LINK,
                new Locator.GetByRoleOptions().setName("Удалить тему")).click();
    }
}
