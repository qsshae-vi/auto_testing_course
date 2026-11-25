package ru.ksu.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class PostComponent {  // PAGE ELEMENT

    private final Page page;
    private final Locator root;
    private final Locator menuTrigger;

    public PostComponent(Locator root) {    // root - контейнер конкретного поста
        this.root = root;
        this.page = root.page();
        this.menuTrigger = root.getByRole(
                AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Меню")  // Кнопка с тремя точками
        );
    }

    public Locator getRoot() {
        return root;
    }

    public void deletePost() {
        // выпадающее меню
        menuTrigger.click();

        // «Удалить тему» из меню
        page.getByText("Удалить тему", new Page.GetByTextOptions().setExact(true))
                .locator("visible=true")
                .click();

        // кнопка подтверждения «Удалить тему» внутри появившейся заглушки
        Locator confirmDeleteBtn = page.locator(".delete-stub a.form-actions_yes").locator("visible=true");
        confirmDeleteBtn.click();
    }
}
