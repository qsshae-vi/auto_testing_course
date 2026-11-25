package ru.ksu.flows;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import ru.ksu.components.DraftComponent;
import ru.ksu.components.TopicEditorOverlay;

public class DraftFlow {    // дополнительный паттерн Facade - для ИСКЛЮЧЕНИЯ ДУБЛИРОВАНИЯ метода удаления черновика в  тестовых классах

    private final DraftComponent draft;
    private final TopicEditorOverlay editor;

    public DraftFlow(Page page) {
        draft = new DraftComponent(page);
        editor = new TopicEditorOverlay(page);
    }

    public void clearIfNeeded() {
        if (editor.isOpen()) {
            Allure.step("Закрыть редактор", () -> editor.closeEditor());
        }

        if (!draft.isExists()) {
            Allure.step("Черновик отсутствует - очистка не требуется");
            return;
        }

        Allure.step("Очищаем имеющийся черновик", () -> draft.open().clearDraft().closeEditor()); // CHAIN OF INVOCATIONS
    }
}