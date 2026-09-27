# user-service

Консольное приложение на Java для управления пользователями (CRUD).

## Стек

- Java 17
- Hibernate 6.5.2 (ORM, без Spring)
- PostgreSQL 18
- Maven
- SLF4J + Logback (логирование)

## Архитектура

- `entity/User` — сущность (JPA-аннотации)
- `dao/UserDao`, `dao/UserDaoImpl` — DAO-паттерн, работа с БД через Hibernate
- `service/UserService` — валидация и бизнес-логика
- `util/HibernateUtil` — SessionFactory (Singleton)
- `Main` — консольный интерфейс

## Функционал

- Создание пользователя (Create)
- Поиск по id (Read)
- Список всех пользователей (Read)
- Обновление (Update)
- Удаление (Delete)
- Поиск по email (Read)

## Запуск

1. Установить PostgreSQL, создать БД:
   ```sql
   CREATE DATABASE user_service_db;