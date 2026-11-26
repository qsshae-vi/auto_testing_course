package ru.ksu.core;

import io.qameta.allure.Allure;

public abstract class LoadableComponent { //<T extends LoadableComponent<T>> {   // LOADABLE COMPONENT (абстрактный класс с generic)
    protected abstract void load(); // наследник реализует действие для загрузки

    protected abstract boolean isLoaded();

    protected abstract void waitUntilLoaded();

    public final void get() {   // final для запрета переопределения
        boolean loaded = Allure.step(
                "Проверить, загружена ли страница",
                () -> isLoaded()
        );

        if (!loaded) {
            Allure.step("Открыть страницу", () -> load());
        }

        Allure.step("Дождаться загрузки страницы", () -> waitUntilLoaded());
    } 
}