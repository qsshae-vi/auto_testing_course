package ru.ksu.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

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

    public Locator getGroupHeading() {
        return groupHeading;
    }

    public void openTopicEditor() {
        topicLink.click();
    }

    public Locator getPostByText(String text) {
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }
}