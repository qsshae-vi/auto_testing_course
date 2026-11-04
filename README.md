# Домашнее задание №4. UI-автотесты функциональности создания поста в группе в Одноклассниках

Автоматизированное UI-тестирование группы в социальной сети [Одноклассники](https://ok.ru) с использованием **Playwright Java, JUnit 5, Page Object Model и Allure**.

## Технологии

- Java 17
- Maven
- Playwright Java 1.58.0
- JUnit 5
- Allure Report

## Подготовка окружения

Для запуска тестов необходимы JDK 17 и Maven.

Установить зависимости проекта:

```bash
mvn clean test-compile
```

## Настройка авторизации

Для тестирования редактора публикаций требуется авторизация в Одноклассниках.

Авторизация выполняется вручную, а состояние браузерной сессии сохраняется с помощью Playwright Codegen.

**1. Создать директорию для данных авторизации**

В корне проекта:

```powershell
New-Item -ItemType Directory -Force playwright/.auth
```

**2. Запустить Playwright Codegen**

В терминале IntelliJ IDEA выполнить:

```powershell
mvn exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.classpathScope=test" "-Dexec.args=codegen --save-storage=playwright/.auth/user.json https://ok.ru"
```

Откроется окно браузера Playwright.

**3. Авторизоваться**

В открывшемся браузере войти в свой аккаунт Одноклассников и убедиться, что авторизация прошла успешно.

**4. Сохранить состояние сессии**

Закрыть окно браузера Codegen после успешной авторизации.

Playwright сохранит состояние авторизации в:

```text
playwright/.auth/user.json
```

**5. Подписаться на группу**

Подписаться на группу https://ok.ru/group/70000060453292, для предоставления прав публикования тем


**6. Запустить тесты**

```bash
mvn clean test
```

Тесты автоматически загрузят сохранённое состояние авторизации через `Browser.NewContextOptions.setStorageStatePath()`.

## Запуск тестов

Запуск всех тестов:

```bash
mvn clean test
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
│   │   └── TopicEditorOverlay.java
│   ├── pages/
│   │   └── GroupPage.java
│   └── tests/
│       └── GroupPostTest.java
└── resources/
    └── allure.properties
```

## Реализованные тесты

1. Открытие страницы группы.
2. Открытие редактора новой темы.
3. Ввод текста в редактор.
4. Очистка введённого текста.
5. Проверка недоступности публикации пустой темы.

Тесты выполняются с отдельными контекстами браузера и не зависят от порядка запуска.
