package ru.ksu.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public final class PostComponent {  // PAGE ELEMENT

    private final Page page;
    private final Locator root;
    private final Locator menuTrigger;
    private final Locator deleteTopicBtn;
    private final Locator confirmDeleteBtn;

    public PostComponent(Locator root) {    // root - контейнер конкретного поста
        this.root = root;
        this.page = root.page();
        this.menuTrigger = root.getByRole(
                AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Меню"));  // Кнопка с тремя точками
        this.deleteTopicBtn = page.getByText("Удалить тему", new Page.GetByTextOptions().setExact(true));

        // кнопка подтверждения «Удалить тему» внутри появившейся заглушки
        this.confirmDeleteBtn = root.locator(".delete-stub a.form-actions_yes");
    }

    public Locator getRoot() {
        return root;
    }

    public void deletePost() {
        // выпадающее меню
        menuTrigger.click();

        // «Удалить тему» из меню
        deleteTopicBtn.click();

        // подтвердить удаление темы
        confirmDeleteBtn.click();
    }
}
