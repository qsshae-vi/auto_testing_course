# Домашнее задание №7. Стратегии поиска элементов в UI-автотестах

Продолжение проекта по автоматизированному UI-тестированию социальной сети [Одноклассники](https://ok.ru/).

В рамках домашнего задания выполнен рефакторинг локаторов существующих UI-автотестов. 

## Технологии

- Java 17
- Maven
- Playwright Java 1.58.0
- JUnit 5
- Allure Report

## Стратегия выбора локаторов

При выборе локаторов использовались следующие принципы:

1. **Приоритет семантических локаторов.** Для интерактивных элементов предпочтение отдавалось `getByRole()`, `getByLabel()` и `getByText()`, позволяющим находить элементы по роли и доступному имени или отображаемому тексту.

2. Для элементов без подходящих семантических атрибутов использовались селекторы по `id`, CSS-классам и HTML-атрибутам.

3. **Комбинирование условий через `and()`.** 

4. **Ограничение области поиска.** Элементы редактора искались относительно его корневого контейнера `editorDialog`, а элементы конкретной публикации - относительно `root`.

5. Локаторы подбирались с учётом фактической DOM-разметки приложения.

6. **Учёт динамических элементов интерфейса.** При работе с выпадающим меню публикации учитывалось, что его содержимое может размещаться за пределами DOM-контейнера конкретного поста.

Для анализа HTML-разметки использовались Chrome DevTools (Elements), а также сообщения Playwright об ошибках поиска элементов.

## Доработанные локаторы

### 1. GroupPage.java

**Заголовок группы и ссылка создания темы**

Для поиска заголовка и ссылки используются семантические локаторы:

```java
page.getByRole(AriaRole.HEADING, ...);
page.getByRole(AriaRole.LINK, ...);
```

**Поиск опубликованной темы**

Для поиска публикации используется CSS-селектор по атрибуту `data-feed-id` с дополнительной фильтрацией по тексту:

```java
Locator postLocator = page.locator("[data-feed-id]")
        .filter(new Locator.FilterOptions()
                .setHasText(text));
```

Вместо зависимости от CSS-класса контейнера `.feed-w` используется атрибут, связанный с идентификатором публикации.

Поскольку значение идентификатора формируется динамически, проверяется наличие атрибута без привязки к его конкретному значению.

Фильтрация `setHasText(text)` позволяет найти нужную публикацию по уникальному тексту, сформированному с использованием UUID.

### 2. TopicEditorOverlay.java

**Корневой контейнер редактора**

Для идентификации модального окна используется комбинирование семантического локатора и CSS-селектора по id через `and()`:

```java
this.editorDialog = page.getByRole(AriaRole.DIALOG)
        .and(page.locator("[id='mtLayer']"));
```

**Поле ввода текста**

Поиск выполняется внутри контейнера редактора:

```java
this.textField = editorDialog
        .locator("section[aria-label='Текст']")
        .getByRole(AriaRole.TEXTBOX);
```

Используется сочетание CSS-селектора по тегу и атрибуту с семантическим поиском текстового поля.

**Кнопка публикации**

Для кнопки «Поделиться» применено комбинирование роли, имени и CSS-класса:

```java
this.publishButton = editorDialog.getByRole(
        AriaRole.BUTTON,
        new Locator.GetByRoleOptions().setName("Поделиться")
).and(page.locator(".posting_submit"));
```

**Кнопка закрытия редактора**

Поиск ограничен контейнером модального окна:

```java
this.closeButton = editorDialog.getByRole(
        AriaRole.BUTTON,
        new Locator.GetByRoleOptions().setName("Закрыть")
);
```

**Кнопка удаления черновика**

Для идентификации кнопки используются роль, имя и атрибут `data-action`:

```java
this.deleteDraft = page.getByRole(
        AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Удалить черновик")
).and(page.locator("button[data-action='clearDraft']"));
```

### 3. PostComponent.java

Для взаимодействия с публикацией используется Page Component с корневым локатором `root`.

**Открытие меню публикации**

```java
this.menuTrigger = root.getByRole(
        AriaRole.BUTTON,
        new Locator.GetByRoleOptions().setName("Меню"));
```

Кнопка ищется относительно конкретной публикации по роли и доступному имени.

**Выбор действия удаления**

```java
this.deleteTopicBtn = page.getByText(
        "Удалить тему",
        new Page.GetByTextOptions().setExact(true));
```

Используется точное совпадение текста. Поиск выполняется относительно `page`, поскольку выпадающее меню может отображаться за пределами контейнера публикации.

**Подтверждение удаления**

```java
this.confirmDeleteBtn = root.locator(
        ".delete-stub a.form-actions_yes");
```

Используется составной CSS-селектор, который ищет ссылку подтверждения внутри блока, появляющегося после выбора действия удаления.

### 4. DraftComponent.java

Для поиска сохранённого черновика используется локатор по имени:

```java
this.draft = page.getByLabel("Продолжить тему...");
```

## Реализованные тесты

Сохранены сценарии предыдущих домашних заданий:

1. Открытие страницы группы и проверка отображения заголовка.
2. Открытие редактора новой темы.
3. Проверка отображения и доступности поля ввода.
4. Ввод текста в редактор с использованием параметризованных тестов (обычный текст, цифры, специальные символы).
5. Очистка введённого текста.
6. Проверка недоступности публикации при пустом поле ввода.
7. Публикация текстовой темы в группе.
8. Проверка появления опубликованного поста в ленте.
9. Удаление тестовой публикации.
10. Очистка сохранённых черновиков.

Сохранена архитектура с использованием Page Object, Page Component, Loadable Component, Value Object, Chain of Invocations, Data Provider и Facade.

## Подготовка окружения

Для запуска тестов необходимы JDK 17 и Maven.

Установить зависимости проекта:

```bash
mvn clean test-compile
```

## Настройка авторизации

Для тестирования функциональности публикации требуется авторизация в Одноклассниках.

**1. Создать директорию для данных авторизации**

```powershell
New-Item -ItemType Directory -Force playwright/.auth
```

**2. Запустить Playwright Codegen**

```powershell
mvn exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.classpathScope=test" "-Dexec.args=codegen --save-storage=playwright/.auth/user.json https://ok.ru"
```

**3. Авторизоваться**

В открывшемся браузере войти в аккаунт Одноклассников.

**4. Сохранить состояние сессии**

Закрыть окно браузера Codegen после успешной авторизации.

Состояние сессии будет сохранено в `playwright/.auth/user.json`.

**5. Вступить в тестируемую группу**

Для воспроизведения сценариев необходимо вступить в [тестируемую группу](https://ok.ru/group/70000060453292).

Файл авторизации исключён из Git через `.gitignore`.

## Запуск тестов

Запуск всех тестов:

```bash
mvn clean test
```

Запуск тестов отдельного класса:

```bash
mvn -Dtest=GroupPostTest test
```

```bash
mvn -Dtest=TopicPublishingTest test
```

Запуск тестов нескольких классов:

```bash
mvn -Dtest=GroupPostTest,TopicPublishingTest test
```

Запуск тестов определённого пакета:

```bash
mvn -Dtest="**/ru/ksu/tests/*Test.java" test
```

## Отчётность Allure

После выполнения тестов сформировать и открыть отчёт:

```bash
mvn allure:serve
```

## Структура проекта

```text
src/test/
├── java/ru/ksu/
│   ├── components/
│   │   ├── DraftComponent.java
│   │   ├── PostComponent.java
│   │   └── TopicEditorOverlay.java
│   ├── core/
│   │   └── LoadableComponent.java
│   ├── data/
│   │   └── TopicTextDataProvider.java
│   ├── flows/
│   │   └── DraftFlow.java
│   ├── model/
│   │   └── TopicText.java
│   ├── pages/
│   │   └── GroupPage.java
│   └── tests/
│       ├── GroupPostTest.java
│       └── TopicPublishingTest.java
└── resources/
    └── allure.properties
```

Тестовые публикации удаляются после выполнения сценариев. Предусмотрена очистка сохранённых черновиков.
