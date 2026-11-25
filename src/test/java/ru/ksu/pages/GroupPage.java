package ru.ksu.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import ru.ksu.components.DraftComponent;
import ru.ksu.components.PostComponent;
import ru.ksu.core.LoadableComponent;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public final class GroupPage extends LoadableComponent { //<GroupPage> {

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

    // LOADABLE COMPONENT
    @Override
    protected void load() {
        page.navigate(URL);
    }

    @Override
    protected boolean isLoaded() {
        return page.url().contains(URL);
    }

    @Override
    protected void waitUntilLoaded() {
        assertThat(groupHeading).isVisible();
    }


    // Методы взаимодействия со страницей
    public void reload() {
       load();
       waitUntilLoaded();
    }

    public Locator getGroupHeading() {
        return groupHeading;
    }

    public void openTopicEditor() {
        topicLink.click();
    }

    public PostComponent getPost(String text) {

        Locator postLocator = page.locator("[data-feed-id]")
                .filter(new Locator.FilterOptions()
                        .setHasText(text));

        return new PostComponent(postLocator);  // создаём и возвращаем публикацию (PAGE ELEMENT)
    }

    public DraftComponent getDraft() {
        return new DraftComponent(page);   // создаём и возвращаем черновик
    }
}