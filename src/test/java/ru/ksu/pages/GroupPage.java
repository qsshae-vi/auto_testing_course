package ru.ksu.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import ru.ksu.components.DraftComponent;
import ru.ksu.components.PostComponent;

public final class GroupPage {

    public static final String URL =
            "https://ok.ru/group/70000060453292";

    private final Page page;
    private final Locator groupHeading;
    private final Locator topicLink;

    public GroupPage(Page page) {
        this.page = page;
        this.groupHeading = page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Лучшая группа по интересам про автомобили"));
        this.topicLink = page.getByRole(
                AriaRole.LINK,
                new Page.GetByRoleOptions()
                        .setName("Создать новую тему")
        );
    }

    // методы взаимодействия со страницей
    public void open() {
        page.navigate(URL);
    }

    public void reload() {
        page.reload();
    }

    public Locator getGroupHeading() {
        return groupHeading;
    }

    public void openTopicEditor() {
        topicLink.click();
    }

    public PostComponent getPost(String text) {

        Locator postLocator = page.locator(".feed-w")
                .filter(new Locator.FilterOptions()
                        .setHasText(text));

        return new PostComponent(postLocator);  // создаём и возвращаем публикацию
    }

    public DraftComponent getDraft() {
        return new DraftComponent(page);   // создаём и возвращаем черновик
    }
}