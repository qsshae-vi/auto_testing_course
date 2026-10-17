# Simple Books API - Automated API Testing

## Документация API

- [Simple Books API - описание эндпоинтов](https://github.com/vdespa/introduction-to-postman-course/blob/main/simple-books-api.md)
- [Simple Books API - коллекция Postman](https://www.postman.com/image-nation/simple-books-api/documentation/veich4u/simplebooksapi-demo)

## Технологии

- **Java 17** - язык программирования
- **REST Assured** - отправка HTTP-запросов и обработка ответов API
- **JUnit 5** - организация и выполнение тестов
- **Allure** - формирование отчётов о тестировании
- **Maven** - управление зависимостями и запуск тестов

## Тестируемые сценарии

Реализованы позитивные, негативные, граничные и валидационные проверки.

| Категория | Сценарий | Ожидаемый результат |
|---|---|---|
| Positive | Получение существующей книги по ID | `200 OK`, корректные данные книги |
| Positive | Создание заказа и последующее получение его данных | `201 Created`, затем `200 OK` |
| Positive | Фильтрация книг по типу | `200 OK`, соответствие книг заданному типу |
| Negative | Получение заказов без авторизации | `401 Unauthorized` |
| Boundary | Запрос книг с `limit=20` и `limit=21` | `200 OK` и `400 Bad Request` |
| Validation | Создание заказа без обязательного `bookId` | `400 Bad Request` |

Для проверки нескольких входных значений используются параметризованные тесты JUnit 5.

## Структура проекта

```text
src/
└── test/
    ├── java/ru/ksu/
    │   ├── clients/
    │   │   ├── AuthClient.java
    │   │   ├── BookClient.java
    │   │   └── OrderClient.java
    │   ├── config/
    │   │   └── Config.java
    │   └── tests/
    │       └── SimpleBooksApiTest.java
    └── resources/
        └── allure.properties
```

- `clients` - методы взаимодействия с API.
- `config` - общая конфигурация HTTP-запросов и авторизации.
- `tests` - тестовые сценарии и проверки ответов.
- `resources` - настройки Allure.

## Запуск тестов

**Требования:** установленные JDK 17 и Apache Maven.

1. Клонировать репозиторий:

   ```bash
   git clone https://github.com/qsshae-vi/auto_testing_course.git
   ```

2. Перейти в каталог проекта и выбрать ветку домашнего задания:

   ```bash
   cd auto_testing_course
   git switch homework-1
   ```

3. Запустить тесты:

   ```bash
   mvn clean test
   ```

## Отчёт Allure

После выполнения тестов результаты сохраняются в `target/allure-results`.

Для просмотра отчёта выполнить:

```bash
mvn allure:serve
```

При неуспешной JUnit-проверке к результату теста прикладывается JSON-ответ сервера для упрощения анализа ошибки.
