# Описание системы: Kanban Board Application

## Оглавление

1. [Общий обзор](#1-общий-обзор)
2. [Архитектура системы](#2-архитектура-системы)
3. [Стек технологий](#3-стек-технологий)
4. [Структура проекта](#4-структура-проекта)
5. [Слой данных — Entities](#5-слой-данных--entities)
6. [Enumerations](#6-enumerations)
7. [Составные ключи (Embeddable)](#7-составные-ключи-embeddable)
8. [Data Transfer Objects (DTO)](#8-data-transfer-objects-dto)
9. [Payloads (входящие запросы)](#9-payloads-входящие-запросы)
10. [Репозитории (Repositories)](#10-репозитории-repositories)
11. [Сервисы (Services)](#11-сервисы-services)
12. [Контроллеры и REST API](#12-контроллеры-и-rest-api)
13. [Безопасность (Security)](#13-безопасность-security)
14. [Маперы (MapStruct)](#14-маперы-mapstruct)
15. [Обработка ошибок](#15-обработка-ошибок)
16. [Фронтенд — архитектура](#16-фронтенд--архитектура)
17. [Управление состоянием](#17-управление-состоянием)
18. [API-клиент (Frontend)](#18-api-клиент-frontend)
19. [Схема базы данных](#19-схема-базы-данных)
20. [Жизненный цикл запроса](#20-жизненный-цикл-запроса)
21. [Ключевые сценарии работы](#21-ключевые-сценарии-работы)

---

## 1. Общий обзор

**Название:** Kanban Board Application  
**Назначение:** Веб-приложение для совместного управления задачами в команде по методологии Kanban.  
**Команда разработки:** Парфёнов Роман, Степанцева Екатерина  
**Тип:** Full-stack веб-приложение  
**Архитектурный подход:** Монорепозиторий, клиент-серверная архитектура (SPA + REST API)

**Основные возможности:**
- Регистрация и аутентификация пользователей
- Создание команд (групп) и управление участниками
- Приглашение в команду по email или по ссылке с ограниченным сроком действия
- Создание Kanban-досок, привязанных к группе
- Гибкое управление колонками (перетаскивание, переименование, изменение порядка)
- Создание задач с приоритетом, статусом, исполнителем и дедлайном
- Перетаскивание задач между колонками (drag-and-drop)
- Комментирование задач
- Прикрепление файлов к задачам
- История изменений задачи (аудит)
- Управление профилем (аватар, имя, пароль)

---

## 2. Архитектура системы

```
┌─────────────────────────────────────────────┐
│                  CLIENT (SPA)               │
│   React + Vite + TanStack Query + Zustand   │
│   Drag-and-drop (dnd-kit), Framer Motion    │
└─────────────────┬───────────────────────────┘
                  │  HTTP (JSON / multipart)
                  │  Cookie-based JWT Auth
┌─────────────────▼───────────────────────────┐
│               BACKEND (REST API)            │
│           Spring Boot 4.0.2 / Java 21       │
│  Controllers → Services → Repositories      │
│  Spring Security + JWT                      │
│  MapStruct mappers, Liquibase migrations     │
└─────────────────┬───────────────────────────┘
                  │  JPA / Hibernate / JDBC
┌─────────────────▼───────────────────────────┐
│            DATABASE (MySQL 8.0+)            │
│  11 таблиц, UTF-8MB4, составные ключи,      │
│  индексы, FK constraints                   │
└─────────────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         FILE STORAGE (local disk)           │
│  Папка uploads/ — аватары и вложения        │
└─────────────────────────────────────────────┘
```

**Слои бэкенда:**

| Слой | Пакет | Ответственность |
|------|-------|-----------------|
| Controller | `controllers/` | Принимает HTTP-запросы, валидирует входящие данные, делегирует сервисам |
| Service | `services/` | Бизнес-логика, транзакции, проверки прав доступа |
| Repository | `repositories/` | CRUD-операции с БД через Spring Data JPA |
| Entity | `entities/` | JPA-сущности, отражающие таблицы БД |
| DTO | `dto/` | Объекты для передачи данных клиенту |
| Payload | `payloads/` | Объекты для приёма данных от клиента |
| Mapper | `mappers/` | Конвертация Entity ↔ DTO (MapStruct) |
| Security | `security/` | JWT-аутентификация, Spring Security |

---

## 3. Стек технологий

### Backend

| Компонент | Технология | Версия |
|-----------|-----------|--------|
| Framework | Spring Boot | 4.0.2 |
| Язык | Java | 21 |
| БД | MySQL | 8.0+ |
| ORM | Hibernate (через Spring Data JPA) | — |
| Миграции | Liquibase | 5.0.1 |
| Маппинг | MapStruct | 1.5.5 |
| JWT | JJWT (io.jsonwebtoken) | 0.11.5 |
| Валидация | Jakarta Validation | — |
| Безопасность | Spring Security | — |
| Хэширование | BCrypt | — |
| Сборка | Maven | — |

### Frontend

| Компонент | Технология | Версия |
|-----------|-----------|--------|
| Framework | React | 19.2.4 |
| Сборщик | Vite | 8.0.4 |
| Роутинг | React Router | 7.14.1 |
| Глобальный стейт | Zustand | 5.0.12 |
| Кэш данных | TanStack React Query | 5.99.0 |
| HTTP | Axios | 1.15.0 |
| Формы | React Hook Form | 7.72.1 |
| Валидация схем | Zod | 4.3.6 |
| Drag & Drop | dnd-kit | — |
| Анимации | Framer Motion | 12.38.0 |
| Стили | Tailwind CSS | 3.4.19 |
| Уведомления | Sonner | — |
| Даты | date-fns | 4.1.0 |
| Иконки | Lucide React | — |
| Markdown | react-markdown + remark-gfm | — |

---

## 4. Структура проекта

```
Kursovik/
├── src/
│   ├── Backend/
│   │   ├── pom.xml
│   │   └── src/main/
│   │       ├── java/ru/ispi/kanban/
│   │       │   ├── configs/          # Конфигурация Spring (web, file, auditing)
│   │       │   ├── constants/        # Константы приложения
│   │       │   ├── controllers/      # REST-контроллеры
│   │       │   ├── dto/              # DTO для ответов
│   │       │   ├── entities/         # JPA-сущности
│   │       │   ├── enums/            # Перечисления
│   │       │   ├── exceptions/       # Кастомные исключения
│   │       │   ├── listeners/        # Слушатели событий Spring
│   │       │   ├── mappers/          # MapStruct-маперы
│   │       │   ├── payloads/         # Входящие DTO (запросы)
│   │       │   ├── repositories/     # Spring Data JPA
│   │       │   ├── security/         # Spring Security + JWT
│   │       │   ├── services/         # Интерфейсы и реализации
│   │       │   └── utils/            # Утилиты
│   │       └── resources/
│   │           ├── application.yml   # Конфигурация приложения
│   │           └── db/changelog/     # Liquibase-скрипты
│   ├── Frontend/
│   │   ├── package.json
│   │   ├── vite.config.js
│   │   └── src/
│   │       ├── api/                  # Axios-клиент и API-методы
│   │       ├── components/           # React-компоненты
│   │       ├── hooks/                # Кастомные хуки (React Query)
│   │       ├── pages/                # Страницы (роуты)
│   │       ├── store/                # Zustand-сторы
│   │       ├── utils/                # Вспомогательные функции
│   │       ├── App.jsx               # Корневой компонент + роутинг
│   │       └── main.jsx              # Точка входа React
│   ├── Database/
│   │   └── CreateDBandTables.sql    # SQL-схема
│   └── docker-compose.yml
└── Paperwork/                        # Документация
```

---

## 5. Слой данных — Entities

Все сущности находятся в пакете `ru.ispi.kanban.entities`.

---

### 5.1 User

**Файл:** `entities/User.java`  
**Таблица:** `users`  
**Описание:** Представляет зарегистрированного пользователя системы.

#### Поля

| Поле | Тип | Аннотации / Ограничения | Описание |
|------|-----|------------------------|----------|
| `id` | `Integer` | `@Id`, `@GeneratedValue(IDENTITY)` | Первичный ключ |
| `email` | `String` | `@Column(unique=true, nullable=false)` | Уникальный email |
| `name` | `String` | `@Column(nullable=false)` | Отображаемое имя |
| `passwordHash` | `String` | `@Column(nullable=false)` | BCrypt-хэш пароля |
| `avatarUrl` | `String` | `@Column(nullable=true)` | Относительный путь к аватару |
| `createdAt` | `LocalDateTime` | `@Column`, устанавливается в `@PrePersist` | Дата регистрации |

#### Методы (для диаграммы классов)

```
+ getId() : Integer
+ getEmail() : String
+ getName() : String
+ getPasswordHash() : String
+ getAvatarUrl() : String
+ getCreatedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setEmail(email: String) : void
+ setName(name: String) : void
+ setPasswordHash(hash: String) : void
+ setAvatarUrl(url: String) : void
+ setCreatedAt(dt: LocalDateTime) : void
```

---

### 5.2 GroupTeam

**Файл:** `entities/GroupTeam.java`  
**Таблица:** `groups_team`  
**Описание:** Команда (группа) пользователей. Является корневым агрегатом для досок и участников.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `name` | `String` | `NOT NULL` | Название группы |
| `description` | `String` | nullable | Описание группы |
| `createdAt` | `LocalDateTime` | auto-set | Дата создания |

#### Методы

```
+ getId() : Integer
+ getName() : String
+ getDescription() : String
+ getCreatedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setName(name: String) : void
+ setDescription(desc: String) : void
+ setCreatedAt(dt: LocalDateTime) : void
```

---

### 5.3 GroupMember

**Файл:** `entities/GroupMember.java`  
**Таблица:** `group_members`  
**Описание:** Связывающая сущность «многие ко многим» между GroupTeam и User. Дополнительно хранит роль участника в группе. Использует составной ключ `GroupMemberId`.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `GroupMemberId` | `@EmbeddedId` | Составной ключ (groupId, userId) |
| `group` | `GroupTeam` | `@ManyToOne`, FK `group_id`, lazy | Ссылка на группу |
| `user` | `User` | `@ManyToOne`, FK `user_id`, lazy | Ссылка на пользователя |
| `role` | `GroupRole` | `@Enumerated(STRING)`, `NOT NULL` | Роль: `admin` или `member` |
| `joinedAt` | `LocalDateTime` | auto-set | Дата вступления |

#### Методы

```
+ getId() : GroupMemberId
+ getGroup() : GroupTeam
+ getUser() : User
+ getRole() : GroupRole
+ getJoinedAt() : LocalDateTime
+ setId(id: GroupMemberId) : void
+ setGroup(group: GroupTeam) : void
+ setUser(user: User) : void
+ setRole(role: GroupRole) : void
+ setJoinedAt(dt: LocalDateTime) : void
```

---

### 5.4 Board

**Файл:** `entities/Board.java`  
**Таблица:** `boards`  
**Описание:** Kanban-доска, принадлежащая группе. Имеет создателя (аудит через `AuditingEntityListener`).

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `group` | `GroupTeam` | `@ManyToOne`, FK `group_id`, `NOT NULL` | Группа-владелец |
| `title` | `String` | `NOT NULL` | Заголовок доски |
| `description` | `String` | nullable, `TEXT` | Описание доски |
| `createdBy` | `User` | `@CreatedBy`, `@ManyToOne`, lazy | Создатель (Spring Auditing) |
| `createdAt` | `LocalDateTime` | auto-set | Дата создания |

#### Аннотации

`@EntityListeners(AuditingEntityListener.class)` — аудит поля `createdBy`.

#### Методы

```
+ getId() : Integer
+ getGroup() : GroupTeam
+ getTitle() : String
+ getDescription() : String
+ getCreatedBy() : User
+ getCreatedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setGroup(group: GroupTeam) : void
+ setTitle(title: String) : void
+ setDescription(desc: String) : void
+ setCreatedBy(user: User) : void
+ setCreatedAt(dt: LocalDateTime) : void
```

---

### 5.5 BoardColumn

**Файл:** `entities/BoardColumn.java`  
**Таблица:** `columns`  
**Описание:** Колонка Kanban-доски. Имеет позицию (для сортировки), которая уникальна в рамках доски.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `board` | `Board` | `@ManyToOne`, FK `board_id`, `NOT NULL` | Доска |
| `title` | `String` | `NOT NULL` | Заголовок колонки |
| `position` | `Long` | `NOT NULL` | Порядковый номер (для drag-and-drop) |
| `createdAt` | `LocalDateTime` | auto-set | Дата создания |

#### Методы

```
+ getId() : Integer
+ getBoard() : Board
+ getTitle() : String
+ getPosition() : Long
+ getCreatedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setBoard(board: Board) : void
+ setTitle(title: String) : void
+ setPosition(position: Long) : void
+ setCreatedAt(dt: LocalDateTime) : void
```

---

### 5.6 BoardUser

**Файл:** `entities/BoardUser.java`  
**Таблица:** `board_users`  
**Описание:** Связь пользователя с доской (контроль доступа на уровне доски). Только члены группы, добавленные на доску, имеют к ней доступ.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `BoardUserId` | `@EmbeddedId` | Составной ключ (boardId, userId) |
| `board` | `Board` | `@ManyToOne`, FK `board_id` | Доска |
| `user` | `User` | `@ManyToOne`, FK `user_id` | Пользователь |

#### Методы

```
+ getId() : BoardUserId
+ getBoard() : Board
+ getUser() : User
+ setId(id: BoardUserId) : void
+ setBoard(board: Board) : void
+ setUser(user: User) : void
```

---

### 5.7 Task

**Файл:** `entities/Task.java`  
**Таблица:** `tasks`  
**Описание:** Задача в Kanban-колонке. Основная рабочая единица системы. Содержит полный набор метаданных: исполнитель, дедлайн, приоритет, статус.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `column` | `BoardColumn` | `@ManyToOne`, FK `column_id`, `NOT NULL` | Колонка |
| `title` | `String` | `NOT NULL` | Заголовок задачи |
| `description` | `String` | nullable, `TEXT` | Описание (поддержка Markdown) |
| `assignee` | `User` | `@ManyToOne`, lazy, nullable | Исполнитель задачи |
| `position` | `Long` | `NOT NULL` | Позиция в колонке |
| `deadline` | `LocalDateTime` | nullable | Дедлайн задачи |
| `priority` | `TaskPriority` | `@Enumerated(STRING)` | Приоритет: `low/medium/high` |
| `status` | `TaskStatus` | `@Enumerated(STRING)` | Статус: `todo/in_progress/done` |
| `createdAt` | `LocalDateTime` | auto-set | Дата создания |

#### Методы

```
+ getId() : Integer
+ getColumn() : BoardColumn
+ getTitle() : String
+ getDescription() : String
+ getAssignee() : User
+ getPosition() : Long
+ getDeadline() : LocalDateTime
+ getPriority() : TaskPriority
+ getStatus() : TaskStatus
+ getCreatedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setColumn(column: BoardColumn) : void
+ setTitle(title: String) : void
+ setDescription(desc: String) : void
+ setAssignee(user: User) : void
+ setPosition(position: Long) : void
+ setDeadline(deadline: LocalDateTime) : void
+ setPriority(priority: TaskPriority) : void
+ setStatus(status: TaskStatus) : void
+ setCreatedAt(dt: LocalDateTime) : void
```

---

### 5.8 Comment

**Файл:** `entities/Comment.java`  
**Таблица:** `comments`  
**Описание:** Комментарий к задаче. Поддерживает редактирование (поле `updatedAt`).

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `task` | `Task` | `@ManyToOne`, FK `task_id`, `NOT NULL` | Задача |
| `user` | `User` | `@ManyToOne`, FK `user_id`, `NOT NULL` | Автор комментария |
| `text` | `String` | `NOT NULL`, `TEXT` | Текст комментария |
| `createdAt` | `LocalDateTime` | auto-set | Дата создания |
| `updatedAt` | `LocalDateTime` | обновляется в `@PreUpdate` | Дата редактирования |

#### Методы

```
+ getId() : Integer
+ getTask() : Task
+ getUser() : User
+ getText() : String
+ getCreatedAt() : LocalDateTime
+ getUpdatedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setTask(task: Task) : void
+ setUser(user: User) : void
+ setText(text: String) : void
+ setCreatedAt(dt: LocalDateTime) : void
+ setUpdatedAt(dt: LocalDateTime) : void
```

---

### 5.9 Attachment

**Файл:** `entities/Attachment.java`  
**Таблица:** `attachments`  
**Описание:** Вложение (файл), прикреплённое к задаче. Файл хранится на диске, в БД сохраняется ключ для поиска.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `task` | `Task` | `@ManyToOne`, FK `task_id`, `NOT NULL` | Задача |
| `user` | `User` | `@ManyToOne`, FK `user_id`, `NOT NULL` | Загрузивший пользователь |
| `fileName` | `String` | `NOT NULL` | Оригинальное имя файла |
| `storageKey` | `String` | `NOT NULL`, `TEXT` | Путь к файлу в хранилище |
| `uploadedAt` | `LocalDateTime` | auto-set | Дата загрузки |

#### Методы

```
+ getId() : Integer
+ getTask() : Task
+ getUser() : User
+ getFileName() : String
+ getStorageKey() : String
+ getUploadedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setTask(task: Task) : void
+ setUser(user: User) : void
+ setFileName(name: String) : void
+ setStorageKey(key: String) : void
+ setUploadedAt(dt: LocalDateTime) : void
```

---

### 5.10 TaskHistory

**Файл:** `entities/TaskHistory.java`  
**Таблица:** `task_history`  
**Описание:** Запись об изменении задачи (аудит-лог). Хранит что изменилось, кем, когда и какие были значения до/после.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `task` | `Task` | `@ManyToOne`, FK `task_id`, `NOT NULL` | Задача |
| `user` | `User` | `@ManyToOne`, FK `user_id`, `NOT NULL` | Пользователь, внёсший изменение |
| `actionType` | `ActionType` | `@Enumerated(STRING)` | Тип действия: create/update/move/delete |
| `changedAttribute` | `String` | nullable | Название изменённого поля |
| `oldValue` | `String` | nullable, `TEXT` | Значение до изменения |
| `newValue` | `String` | nullable, `TEXT` | Значение после изменения |
| `changedAt` | `LocalDateTime` | auto-set | Время изменения |

#### Методы

```
+ getId() : Integer
+ getTask() : Task
+ getUser() : User
+ getActionType() : ActionType
+ getChangedAttribute() : String
+ getOldValue() : String
+ getNewValue() : String
+ getChangedAt() : LocalDateTime
+ setId(id: Integer) : void
+ setTask(task: Task) : void
+ setUser(user: User) : void
+ setActionType(type: ActionType) : void
+ setChangedAttribute(attr: String) : void
+ setOldValue(value: String) : void
+ setNewValue(value: String) : void
+ setChangedAt(dt: LocalDateTime) : void
```

---

### 5.11 Invitation

**Файл:** `entities/Invitation.java`  
**Таблица:** `invitations`  
**Описание:** Приглашение в группу. Может быть двух типов: EMAIL (адресное) и LINK (по публичной ссылке). Имеет срок действия и статус жизненного цикла.

#### Поля

| Поле | Тип | Ограничения | Описание |
|------|-----|-------------|----------|
| `id` | `Integer` | PK, auto-increment | Первичный ключ |
| `group` | `GroupTeam` | `@ManyToOne`, FK `group_id`, `NOT NULL` | Группа |
| `email` | `String` | nullable (null для LINK) | Email получателя |
| `inviteToken` | `String` | `NOT NULL`, unique | Уникальный токен приглашения |
| `type` | `InvitationType` | `@Enumerated(STRING)` | Тип: `EMAIL` или `LINK` |
| `status` | `InvitationStatus` | `@Enumerated(STRING)` | Статус жизненного цикла |
| `createdBy` | `User` | `@ManyToOne`, nullable | Создатель приглашения |
| `createdAt` | `LocalDateTime` | auto-set | Дата создания |
| `expiresAt` | `LocalDateTime` | `NOT NULL` | Дата истечения срока |

#### Методы

```
+ getId() : Integer
+ getGroup() : GroupTeam
+ getEmail() : String
+ getInviteToken() : String
+ getType() : InvitationType
+ getStatus() : InvitationStatus
+ getCreatedBy() : User
+ getCreatedAt() : LocalDateTime
+ getExpiresAt() : LocalDateTime
+ setId(id: Integer) : void
+ setGroup(group: GroupTeam) : void
+ setEmail(email: String) : void
+ setInviteToken(token: String) : void
+ setType(type: InvitationType) : void
+ setStatus(status: InvitationStatus) : void
+ setCreatedBy(user: User) : void
+ setCreatedAt(dt: LocalDateTime) : void
+ setExpiresAt(dt: LocalDateTime) : void
```

---

## 6. Enumerations

**Пакет:** `ru.ispi.kanban.enums`

| Enum | Значения | Описание |
|------|---------|----------|
| `TaskStatus` | `todo`, `in_progress`, `done` | Статус задачи |
| `TaskPriority` | `low`, `medium`, `high` | Приоритет задачи |
| `GroupRole` | `admin`, `member` | Роль участника группы |
| `ActionType` | `create`, `update`, `move`, `delete` | Тип действия в истории задачи |
| `InvitationType` | `EMAIL`, `LINK` | Способ доставки приглашения |
| `InvitationStatus` | `PENDING`, `ACCEPTED`, `DECLINED`, `REVOKED`, `EXPIRED` | Статус приглашения |

**Жизненный цикл InvitationStatus:**
```
PENDING → ACCEPTED
        → DECLINED
        → REVOKED  (отозвано создателем)
        → EXPIRED  (истёк срок expiresAt)
```

---

## 7. Составные ключи (Embeddable)

### GroupMemberId

**Файл:** `entities/GroupMemberId.java`

```java
@Embeddable
class GroupMemberId implements Serializable {
    Integer groupId;
    Integer userId;
    // equals() + hashCode()
}
```

### BoardUserId

**Файл:** `entities/BoardUserId.java`

```java
@Embeddable
class BoardUserId implements Serializable {
    Integer boardId;
    Integer userId;
    // equals() + hashCode()
}
```

---

## 8. Data Transfer Objects (DTO)

**Пакет:** `ru.ispi.kanban.dto`  
Используются для ответов API (Entity → DTO через MapStruct). Не содержат чувствительных данных (например, `passwordHash`).

| Класс | Поля |
|-------|------|
| `UserDto` | `id`, `email`, `name`, `avatarUrl`, `createdAt` |
| `GroupTeamDto` | `id`, `name`, `description`, `createdAt` |
| `GroupMemberDto` | `groupId`, `userId`, `user` (UserDto), `role`, `joinedAt` |
| `BoardDto` | `id`, `group` (GroupTeamDto), `title`, `description`, `createdBy` (UserDto), `createdAt` |
| `ColumnDto` | `id`, `board` (BoardDto), `title`, `position`, `createdAt` |
| `TaskDto` | `id`, `columnId`, `title`, `description`, `assignee` (UserDto), `position`, `deadline`, `priority`, `status`, `createdAt` |
| `CommentDto` | `id`, `task` (TaskDto), `user` (UserDto), `text`, `createdAt`, `updatedAt` |
| `AttachmentDto` | `id`, `task` (TaskDto), `user` (UserDto), `fileName`, `storageKey`, `uploadedAt` |
| `TaskHistoryDto` | `id`, `task` (TaskDto), `user` (UserDto), `actionType`, `changedAttribute`, `oldValue`, `newValue`, `changedAt` |
| `InvitationDto` | `id`, `group` (GroupTeamDto), `email`, `inviteToken`, `type`, `status`, `createdBy` (UserDto), `createdAt`, `expiresAt` |
| `AuthTokensDto` | `accessToken`, `refreshToken` |

---

## 9. Payloads (входящие запросы)

**Пакет:** `ru.ispi.kanban.payloads`  
Входящие данные от клиента. Аннотированы Jakarta Validation (`@NotBlank`, `@Email`, `@Size` и др.).

| Класс | Поля | Применение |
|-------|------|-----------|
| `LoginPayload` | `email`, `password` | POST /auth/login |
| `RegistrationPayload` | `email`, `name`, `password` | POST /auth/registration |
| `UpdateNamePayload` | `name` | PATCH /users/profile/name |
| `UpdatePasswordPayload` | `oldPassword`, `newPassword` | PATCH /users/profile/password |
| `GroupTeamPayload` | `name`, `description` | POST/PUT /groupteams/ |
| `AddMemberToGroupTeamPayload` | `userId`, `role` | POST /group-members/{groupId} |
| `UpdateMemberRoleInGroupTeamPayload` | `role` | PUT /group-members/{groupId}/users/{userId} |
| `CreateEmailInvitationPayload` | `email`, `expiresInDays` | POST /invitations/group/{id}/email |
| `CreateLinkInvitationPayload` | `expiresInDays` | POST /invitations/group/{id}/link |
| `InvitationRespondPayload` | `accept` (boolean) | POST /invitations/{id}/respond |
| `CreateBoardPayload` | `groupId`, `title`, `description` | POST /boards/ |
| `UpdateBoardPayload` | `title`, `description` | PUT /boards/{boardId} |
| `CreateColumnPayload` | `title` | POST /boards/{boardId}/columns/ |
| `UpdateColumnPayload` | `title`, `position` | PUT /boards/{boardId}/columns/{columnId} |
| `CreateTaskPayload` | `columnId`, `title`, `description`, `assigneeId`, `deadline`, `priority`, `status` | POST /boards/{boardId}/tasks/ |
| `UpdateTaskPayload` | `columnId`, `title`, `description`, `assigneeId`, `position`, `deadline`, `priority`, `status` | PUT /boards/{boardId}/tasks/{taskId} |
| `CreateCommentPayload` | `text` | POST /boards/{boardId}/tasks/{taskId}/comments |
| `UpdateCommentPayload` | `text` | PUT /boards/{boardId}/tasks/{taskId}/comments/{commentId} |

---

## 10. Репозитории (Repositories)

**Пакет:** `ru.ispi.kanban.repositories`  
Все репозитории расширяют `JpaRepository<T, ID>` — стандартные методы: `findAll`, `findById`, `save`, `delete`, `existsById`.

| Репозиторий | Сущность | ID-тип | Кастомные методы (примеры) |
|-------------|---------|--------|---------------------------|
| `UserRepository` | `User` | `Integer` | `findByEmail(String email)` |
| `GroupTeamRepository` | `GroupTeam` | `Integer` | `findAllByMembers_User_Id(Integer userId)` |
| `GroupMemberRepository` | `GroupMember` | `GroupMemberId` | `findAllByGroup_Id(Integer groupId)`, `findByGroup_IdAndUser_Id(Integer, Integer)` |
| `BoardRepository` | `Board` | `Integer` | `findAllByGroup_Id(Integer groupId)`, `findAllByBoardUsers_User_Id(Integer userId)` |
| `BoardUserRepository` | `BoardUser` | `BoardUserId` | `findAllByBoard_Id(Integer boardId)`, `existsByBoard_IdAndUser_Id(Integer, Integer)` |
| `ColumnRepository` | `BoardColumn` | `Integer` | `findAllByBoard_IdOrderByPosition(Integer boardId)` |
| `TaskRepository` | `Task` | `Integer` | `findAllByColumn_Board_Id(Integer boardId)`, `findAllByColumn_Id(Integer columnId)`, `findAllByAssignee_Id(Integer userId)` |
| `CommentRepository` | `Comment` | `Integer` | `findAllByTask_Id(Integer taskId)` |
| `AttachmentRepository` | `Attachment` | `Integer` | `findAllByTask_Id(Integer taskId)` |
| `TaskHistoryRepository` | `TaskHistory` | `Integer` | `findAllByTask_IdOrderByChangedAtDesc(Integer taskId)` |
| `InvitationRepository` | `Invitation` | `Integer` | `findByInviteToken(String token)`, `findAllByEmail(String email)`, `findAllByGroup_Id(Integer groupId)` |

---

## 11. Сервисы (Services)

**Пакет:** `ru.ispi.kanban.services`  
Каждый сервис определён как интерфейс + реализация `*Impl`. Реализации аннотированы `@Service`, `@Transactional`.

---

### 11.1 AuthService / AuthServiceImpl

**Ответственность:** Регистрация, вход, выход, обновление токена, проверка аутентификации.

```
+ login(payload: LoginPayload, response: HttpServletResponse) : AuthTokensDto
+ register(payload: RegistrationPayload, response: HttpServletResponse) : AuthTokensDto
+ checkAuth(request: HttpServletRequest) : UserDto
+ refreshToken(request: HttpServletRequest, response: HttpServletResponse) : void
+ logout(response: HttpServletResponse) : void
```

**Нюансы:**
- При входе/регистрации генерируются два JWT-токена (access + refresh) и устанавливаются как HttpOnly-cookies.
- При `refreshToken` читается refresh-cookie, валидируется, генерируется новый access-токен.
- При `logout` обнуляются cookies (max-age=0).

---

### 11.2 UserService / UserServiceImpl

**Ответственность:** Управление профилем пользователя.

```
+ getCurrentUser(principal: CustomUserDetails) : UserDto
+ updateName(principal: CustomUserDetails, payload: UpdateNamePayload) : UserDto
+ updatePassword(principal: CustomUserDetails, payload: UpdatePasswordPayload) : void
+ uploadAvatar(principal: CustomUserDetails, file: MultipartFile) : UserDto
```

---

### 11.3 GroupTeamService / GroupTeamServiceImpl

**Ответственность:** CRUD операций над группами.

```
+ getMyGroups(principal: CustomUserDetails) : List<GroupTeamDto>
+ getGroup(groupId: Integer, principal: CustomUserDetails) : GroupTeamDto
+ createGroup(payload: GroupTeamPayload, principal: CustomUserDetails) : GroupTeamDto
+ updateGroup(groupId: Integer, payload: GroupTeamPayload, principal: CustomUserDetails) : GroupTeamDto
+ deleteGroup(groupId: Integer, principal: CustomUserDetails) : void
```

**Нюансы:** При создании группы создатель автоматически добавляется как `admin` через `GroupMemberService`.

---

### 11.4 GroupMemberService / GroupMemberServiceImpl

**Ответственность:** Управление участниками группы.

```
+ getMembers(groupId: Integer, principal: CustomUserDetails) : List<GroupMemberDto>
+ addMember(groupId: Integer, payload: AddMemberToGroupTeamPayload, principal: CustomUserDetails) : GroupMemberDto
+ updateRole(groupId: Integer, userId: Integer, payload: UpdateMemberRoleInGroupTeamPayload, principal: CustomUserDetails) : GroupMemberDto
+ removeMember(groupId: Integer, userId: Integer, principal: CustomUserDetails) : void
```

---

### 11.5 GroupPermissionService

**Ответственность:** Проверка ролей в группе. Используется другими сервисами.

```
+ requireAdmin(groupId: Integer, userId: Integer) : void
+ requireMember(groupId: Integer, userId: Integer) : void
+ isAdmin(groupId: Integer, userId: Integer) : boolean
+ isMember(groupId: Integer, userId: Integer) : boolean
```

---

### 11.6 InvitationService / InvitationServiceImpl

**Ответственность:** Создание, отзыв, принятие/отклонение приглашений.

```
+ createEmailInvitation(groupId: Integer, payload: CreateEmailInvitationPayload, principal: CustomUserDetails) : InvitationDto
+ createLinkInvitation(groupId: Integer, payload: CreateLinkInvitationPayload, principal: CustomUserDetails) : InvitationDto
+ getGroupInvitations(groupId: Integer, principal: CustomUserDetails) : List<InvitationDto>
+ revokeInvitation(invitationId: Integer, principal: CustomUserDetails) : void
+ getMyInvitations(principal: CustomUserDetails) : List<InvitationDto>
+ respondToInvitation(invitationId: Integer, payload: InvitationRespondPayload, principal: CustomUserDetails) : InvitationDto
+ joinViaLink(token: String, principal: CustomUserDetails) : InvitationDto
```

**Нюансы:**
- `createEmailInvitation` генерирует UUID-токен, устанавливает дату истечения = now + expiresInDays.
- `joinViaLink` ищет приглашение по токену, проверяет статус `PENDING` и срок, добавляет пользователя в группу.
- При принятии EMAIL-приглашения (`respond(accept=true)`) пользователь добавляется в группу как `member`.

---

### 11.7 BoardService / BoardServiceImpl

**Ответственность:** CRUD досок, управление доступом.

```
+ getUserBoards(principal: CustomUserDetails) : List<BoardDto>
+ getBoardsByGroup(groupId: Integer, principal: CustomUserDetails) : List<BoardDto>
+ getBoard(boardId: Integer, principal: CustomUserDetails) : BoardDto
+ createBoard(payload: CreateBoardPayload, principal: CustomUserDetails) : BoardDto
+ updateBoard(boardId: Integer, payload: UpdateBoardPayload, principal: CustomUserDetails) : BoardDto
+ deleteBoard(boardId: Integer, principal: CustomUserDetails) : void
```

---

### 11.8 BoardUserService / BoardUserServiceImpl

**Ответственность:** Добавление/удаление пользователей с доски.

```
+ getBoardUsers(boardId: Integer, principal: CustomUserDetails) : List<UserDto>
+ addUserToBoard(boardId: Integer, userId: Integer, principal: CustomUserDetails) : void
+ removeUserFromBoard(boardId: Integer, userId: Integer, principal: CustomUserDetails) : void
```

---

### 11.9 ColumnService / ColumnServiceImpl

**Ответственность:** Управление колонками доски.

```
+ getColumns(boardId: Integer, principal: CustomUserDetails) : List<ColumnDto>
+ createColumn(boardId: Integer, payload: CreateColumnPayload, principal: CustomUserDetails) : ColumnDto
+ updateColumn(boardId: Integer, columnId: Integer, payload: UpdateColumnPayload, principal: CustomUserDetails) : ColumnDto
+ deleteColumn(boardId: Integer, columnId: Integer, principal: CustomUserDetails) : void
+ moveColumn(boardId: Integer, columnId: Integer, newPosition: Long, principal: CustomUserDetails) : void
```

**Нюансы:** При создании колонки позиция вычисляется как `max(position) + 1`. При перемещении пересчитываются позиции других колонок.

---

### 11.10 TaskService / TaskServiceImpl

**Ответственность:** CRUD задач, фильтрация, перемещение.

```
+ getTasks(boardId: Integer, principal: CustomUserDetails) : List<TaskDto>
+ getTask(boardId: Integer, taskId: Integer, principal: CustomUserDetails) : TaskDto
+ getTasksByColumn(boardId: Integer, columnId: Integer, principal: CustomUserDetails) : List<TaskDto>
+ getMyTasks(boardId: Integer, principal: CustomUserDetails) : List<TaskDto>
+ getTasksByAssignee(boardId: Integer, assigneeId: Integer, principal: CustomUserDetails) : List<TaskDto>
+ createTask(boardId: Integer, payload: CreateTaskPayload, principal: CustomUserDetails) : TaskDto
+ updateTask(boardId: Integer, taskId: Integer, payload: UpdateTaskPayload, principal: CustomUserDetails) : TaskDto
+ deleteTask(boardId: Integer, taskId: Integer, principal: CustomUserDetails) : void
```

**Нюансы:** При обновлении задачи публикуется `TaskChangeEvent` через ApplicationEventPublisher — `TaskHistoryListener` записывает изменения в `task_history`.

---

### 11.11 CommentService / CommentServiceImpl

**Ответственность:** Управление комментариями к задаче.

```
+ getComments(boardId: Integer, taskId: Integer, principal: CustomUserDetails) : List<CommentDto>
+ createComment(boardId: Integer, taskId: Integer, payload: CreateCommentPayload, principal: CustomUserDetails) : CommentDto
+ updateComment(boardId: Integer, taskId: Integer, commentId: Integer, payload: UpdateCommentPayload, principal: CustomUserDetails) : CommentDto
+ deleteComment(boardId: Integer, taskId: Integer, commentId: Integer, principal: CustomUserDetails) : void
```

**Нюансы:** Удалять и редактировать может только автор комментария.

---

### 11.12 AttachmentService / AttachmentServiceImpl

**Ответственность:** Загрузка, перечисление и удаление вложений.

```
+ getAttachments(boardId: Integer, taskId: Integer, principal: CustomUserDetails) : List<AttachmentDto>
+ uploadAttachment(boardId: Integer, taskId: Integer, file: MultipartFile, principal: CustomUserDetails) : AttachmentDto
+ deleteAttachment(boardId: Integer, taskId: Integer, attachmentId: Integer, principal: CustomUserDetails) : void
```

---

### 11.13 TaskHistoryService / TaskHistoryServiceImpl

**Ответственность:** Запись и чтение истории изменений задачи.

```
+ getHistory(boardId: Integer, taskId: Integer, principal: CustomUserDetails) : List<TaskHistoryDto>
+ recordChange(task: Task, user: User, actionType: ActionType, attribute: String, oldValue: String, newValue: String) : void
```

---

### 11.14 FileStorageService / LocalFileStorageService

**Ответственность:** Абстракция файлового хранилища (сохранение / удаление / загрузка файлов).

```
+ store(file: MultipartFile, folder: String) : String          // возвращает storageKey
+ load(storageKey: String) : Resource
+ delete(storageKey: String) : void
```

**Нюансы:** `LocalFileStorageService` хранит файлы в папке `uploads/{folder}/{uuid}_{originalName}`. Интерфейс допускает замену на S3 без изменения вызывающего кода.

---

## 12. Контроллеры и REST API

**Базовый путь:** `/kanban/api` (настроен через `spring.mvc.servlet.path`)

---

### AuthController — `/auth`

| Метод | URL | Тело запроса | Ответ | Описание |
|-------|-----|-------------|-------|----------|
| POST | `/auth/login` | `LoginPayload` | `AuthTokensDto` + cookies | Вход в систему |
| POST | `/auth/registration` | `RegistrationPayload` | `AuthTokensDto` + cookies | Регистрация |
| GET | `/auth/checkAuth` | — | `UserDto` | Проверка текущей сессии |
| POST | `/auth/refresh` | cookie `refreshTokenKanban` | — | Обновление access-токена |
| POST | `/auth/logout` | — | — | Выход (очистка cookies) |

---

### UserController — `/users/profile`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `/users/profile` | — | `UserDto` | Профиль текущего пользователя |
| PATCH | `/users/profile/name` | `UpdateNamePayload` | `UserDto` | Изменение имени |
| PATCH | `/users/profile/password` | `UpdatePasswordPayload` | — | Смена пароля |
| POST | `/users/profile/avatar` | `multipart/form-data` | `UserDto` | Загрузка аватара |

---

### GroupTeamController — `/groupteams`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `/groupteams/` | — | `List<GroupTeamDto>` | Мои группы |
| GET | `/groupteams/{id}` | — | `GroupTeamDto` | Одна группа |
| POST | `/groupteams/` | `GroupTeamPayload` | `GroupTeamDto` | Создать группу |
| PUT | `/groupteams/{id}` | `GroupTeamPayload` | `GroupTeamDto` | Обновить группу |
| DELETE | `/groupteams/{id}` | — | — | Удалить группу |

---

### GroupMemberController — `/group-members`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `/group-members/{groupId}` | — | `List<GroupMemberDto>` | Участники группы |
| POST | `/group-members/{groupId}` | `AddMemberToGroupTeamPayload` | `GroupMemberDto` | Добавить участника |
| PUT | `/group-members/{groupId}/users/{userId}` | `UpdateMemberRoleInGroupTeamPayload` | `GroupMemberDto` | Изменить роль |
| DELETE | `/group-members/{groupId}/users/{userId}` | — | — | Удалить участника |

---

### InvitationController — `/invitations`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| POST | `/invitations/group/{groupId}/email` | `CreateEmailInvitationPayload` | `InvitationDto` | Отправить email-приглашение |
| POST | `/invitations/group/{groupId}/link` | `CreateLinkInvitationPayload` | `InvitationDto` | Создать ссылку-приглашение |
| GET | `/invitations/group/{groupId}` | — | `List<InvitationDto>` | Приглашения группы |
| DELETE | `/invitations/{invitationId}` | — | — | Отозвать приглашение |
| GET | `/invitations/my` | — | `List<InvitationDto>` | Мои входящие приглашения |
| POST | `/invitations/{invitationId}/respond` | `InvitationRespondPayload` | `InvitationDto` | Принять/отклонить |
| POST | `/invitations/join/{token}` | — | `InvitationDto` | Вступить по ссылке |

---

### BoardController — `/boards`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `/boards/` | — | `List<BoardDto>` | Все мои доски |
| GET | `/boards/group/{groupId}` | — | `List<BoardDto>` | Доски группы |
| GET | `/boards/{boardId}` | — | `BoardDto` | Одна доска |
| POST | `/boards/` | `CreateBoardPayload` | `BoardDto` | Создать доску |
| PUT | `/boards/{boardId}` | `UpdateBoardPayload` | `BoardDto` | Обновить доску |
| DELETE | `/boards/{boardId}` | — | — | Удалить доску |

---

### BoardUserController — `/boards/{boardId}/users`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `/boards/{boardId}/users/` | — | `List<UserDto>` | Пользователи доски |
| POST | `/boards/{boardId}/users/{userId}` | — | — | Добавить пользователя на доску |
| DELETE | `/boards/{boardId}/users/{userId}` | — | — | Удалить пользователя с доски |

---

### ColumnController — `/boards/{boardId}/columns`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `/boards/{boardId}/columns/` | — | `List<ColumnDto>` | Колонки доски |
| POST | `/boards/{boardId}/columns/` | `CreateColumnPayload` | `ColumnDto` | Создать колонку |
| PUT | `/boards/{boardId}/columns/{columnId}` | `UpdateColumnPayload` | `ColumnDto` | Обновить колонку |
| DELETE | `/boards/{boardId}/columns/{columnId}` | — | — | Удалить колонку |
| PUT | `/boards/{boardId}/columns/{columnId}/move` | `?newPosition=X` | — | Переместить колонку |

---

### TaskController — `/boards/{boardId}/tasks`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `/boards/{boardId}/tasks/` | — | `List<TaskDto>` | Все задачи доски |
| GET | `/boards/{boardId}/tasks/{taskId}` | — | `TaskDto` | Одна задача |
| GET | `/boards/{boardId}/tasks/column/{columnId}` | — | `List<TaskDto>` | Задачи колонки |
| GET | `/boards/{boardId}/tasks/my` | — | `List<TaskDto>` | Мои задачи |
| GET | `/boards/{boardId}/tasks/assignee/{assigneeId}` | — | `List<TaskDto>` | Задачи исполнителя |
| POST | `/boards/{boardId}/tasks/` | `CreateTaskPayload` | `TaskDto` | Создать задачу |
| PUT | `/boards/{boardId}/tasks/{taskId}` | `UpdateTaskPayload` | `TaskDto` | Обновить задачу |
| DELETE | `/boards/{boardId}/tasks/{taskId}` | — | — | Удалить задачу |

---

### CommentController — `/boards/{boardId}/tasks/{taskId}/comments`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `.../comments` | — | `List<CommentDto>` | Комментарии задачи |
| POST | `.../comments` | `CreateCommentPayload` | `CommentDto` | Добавить комментарий |
| PUT | `.../comments/{commentId}` | `UpdateCommentPayload` | `CommentDto` | Редактировать комментарий |
| DELETE | `.../comments/{commentId}` | — | — | Удалить комментарий |

---

### AttachmentController — `/boards/{boardId}/tasks/{taskId}/attachments`

| Метод | URL | Тело | Ответ | Описание |
|-------|-----|------|-------|----------|
| GET | `.../attachments` | — | `List<AttachmentDto>` | Вложения задачи |
| POST | `.../attachments` | `multipart/form-data` | `AttachmentDto` | Загрузить файл |
| DELETE | `.../attachments/{attachmentId}` | — | — | Удалить вложение |

---

### TaskHistoryController — `/boards/{boardId}/tasks/{taskId}/history`

| Метод | URL | Ответ | Описание |
|-------|-----|-------|----------|
| GET | `.../history` | `List<TaskHistoryDto>` | История изменений задачи |

---

### FileController — `/files`

| Метод | URL | Ответ | Описание |
|-------|-----|-------|----------|
| GET | `/files/{folder}/{filename}` | `Resource` (file bytes) | Скачать файл |

**Нюансы:** Контроллер проверяет права доступа: аватары (`avatars/`) — только авторизованный пользователь, вложения (`attachments/`) — только участники доски.

---

## 13. Безопасность (Security)

**Пакет:** `ru.ispi.kanban.security`

### Конфигурация Spring Security

**Файл:** `security/SecurityConfig.java`

- **Session Management:** `STATELESS` — HTTP-сессии не используются
- **CSRF:** отключён (REST API с JWT)
- **Публичные endpoint-ы:** `/auth/**`, `/hello`
- **Остальные:** требуют аутентификации
- **Фильтр:** `JwtAuthenticationFilter` (добавлен перед `UsernamePasswordAuthenticationFilter`)
- **PasswordEncoder:** `BCryptPasswordEncoder`

### JWT Service

**Файл:** `security/jwt/JwtService.java`

| Метод | Описание |
|-------|----------|
| `generateAccessToken(user: User) : String` | Создаёт access-токен (короткий срок жизни) |
| `generateRefreshToken(user: User) : String` | Создаёт refresh-токен (длинный срок жизни) |
| `extractUserId(token: String) : Integer` | Извлекает userId из claims |
| `extractEmail(token: String) : String` | Извлекает subject (email) из claims |
| `isTokenValid(token: String) : boolean` | Проверяет подпись и срок жизни |

**Claims токена:**
```json
{
  "sub": "user@email.com",
  "userId": 42,
  "iat": 1700000000,
  "exp": 1700003600
}
```

**Cookie-имена:**
- `accessTokenKanban` — краткосрочный токен доступа
- `refreshTokenKanban` — токен для обновления

### JwtAuthenticationFilter

**Файл:** `security/jwt/JwtAuthenticationFilter.java`  
Извлекает токен из cookie, валидирует, загружает `CustomUserDetails` из `CustomUserDetailsService`, устанавливает `SecurityContextHolder`.

### CustomUserDetails

**Файл:** `security/CustomUserDetails.java`  
Реализует `UserDetails`. Хранит поля: `userId`, `email`, `passwordHash`, `authorities`.

### CustomUserDetailsService

**Файл:** `security/CustomUserDetailsService.java`  
Реализует `UserDetailsService`. Метод `loadUserByUsername(email)` загружает пользователя из `UserRepository`.

### Обработчики ошибок безопасности

| Класс | Ситуация |
|-------|----------|
| `CustomAuthenticationEntryPoint` | Неавторизованный запрос (401) |
| `CustomAccessDeniedHandler` | Недостаточно прав (403) |

---

## 14. Маперы (MapStruct)

**Пакет:** `ru.ispi.kanban.mappers`  
Используют `@Mapper(componentModel = "spring")` — автоматически регистрируются как Spring-бины.

| Маппер | Метод | Направление |
|--------|-------|------------|
| `UserMapper` | `toDto(user: User) : UserDto` | Entity → DTO |
| `GroupTeamMapper` | `toDto(group: GroupTeam) : GroupTeamDto` | Entity → DTO |
| `GroupMemberMapper` | `toDto(member: GroupMember) : GroupMemberDto` | Entity → DTO |
| `BoardMapper` | `toDto(board: Board) : BoardDto` | Entity → DTO |
| `ColumnMapper` | `toDto(column: BoardColumn) : ColumnDto` | Entity → DTO |
| `TaskMapper` | `toDto(task: Task) : TaskDto` | Entity → DTO |
| `CommentMapper` | `toDto(comment: Comment) : CommentDto` | Entity → DTO |
| `AttachmentMapper` | `toDto(attachment: Attachment) : AttachmentDto` | Entity → DTO |
| `TaskHistoryMapper` | `toDto(history: TaskHistory) : TaskHistoryDto` | Entity → DTO |
| `InvitationMapper` | `toDto(invitation: Invitation) : InvitationDto` | Entity → DTO |

---

## 15. Обработка ошибок

**Пакет:** `ru.ispi.kanban.exceptions`

### Иерархия исключений

```
RuntimeException
└── ApiException                       # Базовое исключение API
    ├── EntityNotFound                 # Сущность не найдена
    │   ├── NoSuchUserByIdException
    │   ├── NoSuchUserByEmailException
    │   ├── ColumnNotFoundException
    │   ├── TaskNotFoundException
    │   ├── CommentNotFoundException
    │   ├── AttachmentNotFoundException
    │   └── InvitationNotFoundException
    ├── UserAlreadyExistsException     # Email уже занят
    ├── InvalidPasswordException       # Неверный пароль
    ├── BoardAccessDeniedException     # Нет доступа к доске
    ├── CommentAccessDeniedException   # Нет доступа к комментарию
    ├── InvitationExpiredException     # Приглашение истекло
    ├── NotMemberException             # Не участник группы
    ├── NotAdminException              # Не администратор группы
    ├── MemberAlreadyExistsException   # Уже участник
    └── ValidationException            # Ошибка валидации
```

### GlobalExceptionHandler

**Файл:** `exceptions/GlobalExceptionHandler.java`  
`@RestControllerAdvice` — перехватывает все исключения, возвращает структурированный JSON с HTTP-статусом.

| Исключение | HTTP-статус |
|-----------|------------|
| `EntityNotFound` и наследники | 404 Not Found |
| `UserAlreadyExistsException` | 409 Conflict |
| `InvalidPasswordException` | 400 Bad Request |
| `BoardAccessDeniedException`, `CommentAccessDeniedException` | 403 Forbidden |
| `NotMemberException`, `NotAdminException` | 403 Forbidden |
| `InvitationExpiredException` | 410 Gone |
| `ValidationException`, MethodArgumentNotValid | 422 Unprocessable Entity |
| Прочие | 500 Internal Server Error |

---

## 16. Фронтенд — архитектура

**Пакет:** `src/Frontend/src/`

### Роутинг (App.jsx)

| Маршрут | Компонент | Доступ |
|---------|----------|--------|
| `/` | `LandingPage` | Публичный |
| `/login` | `LoginPage` | Только гости |
| `/register` | `RegisterPage` | Только гости |
| `/join/:token` | `JoinPage` | Публичный |
| `/dashboard` | `DashboardPage` | Авторизованные |
| `/groups/:groupId` | `GroupPage` | Авторизованные |
| `/boards/:boardId` | `BoardPage` | Авторизованные |
| `/profile` | `ProfilePage` | Авторизованные |

**AuthGuard:** компонент-обёртка, перенаправляет неавторизованных на `/login`.

### Страницы (Pages)

| Страница | Функционал |
|---------|-----------|
| `LandingPage` | Публичная страница с описанием продукта |
| `LoginPage` | Форма входа (React Hook Form + Zod) |
| `RegisterPage` | Форма регистрации |
| `DashboardPage` | Список групп + входящие приглашения |
| `GroupPage` | Список досок группы, управление участниками |
| `BoardPage` | Kanban-доска: колонки и задачи (dnd-kit) |
| `ProfilePage` | Редактирование имени, пароля, аватара |
| `JoinPage` | Обработка ссылки-приглашения `?token=...` |

### Компоненты Kanban

**`KanbanBoard.jsx`** — главный компонент доски:
- Инициализирует `DndContext` из dnd-kit
- Управляет состоянием drag-and-drop (active item)
- Различает перетаскивание колонки vs задачи
- Вызывает `useMoveColumn` / `useMoveTask` при сбросе

**`Column.jsx`** — колонка доски:
- `SortableContext` для задач внутри
- `useDroppable` — принимает задачи из других колонок
- Отображает кнопки добавления задачи, переименования, удаления

**`TaskCard.jsx`** — карточка задачи:
- `useSortable` — поддержка перетаскивания
- Отображает: заголовок, приоритет, дедлайн, аватар исполнителя
- При клике открывает `TaskModal`

**`TaskModal.jsx`** — детальный вид задачи:
- Редактирование всех полей задачи
- Вкладки: описание (Markdown), комментарии, вложения, история
- Загрузка файлов (drag-and-drop в input)

---

## 17. Управление состоянием

### authStore (Zustand)

**Файл:** `store/authStore.js`

| Поле / Метод | Тип | Описание |
|-------------|-----|----------|
| `user` | `UserDto \| null` | Текущий пользователь |
| `isAuthenticated` | `boolean` | Флаг авторизации |
| `isChecked` | `boolean` | Флаг завершённой проверки сессии |
| `setUser(user)` | action | Установить пользователя |
| `markChecked()` | action | Отметить, что сессия проверена |
| `logout()` | action | Сбросить состояние |

### uiStore (Zustand)

**Файл:** `store/uiStore.js`

| Поле / Метод | Тип | Описание |
|-------------|-----|----------|
| `sidebarOpen` | `boolean` | Открыт ли sidebar |
| `toggleSidebar()` | action | Переключить sidebar |
| `setSidebarOpen(value)` | action | Установить состояние |

### TanStack React Query

Используется для всех серверных данных (boards, tasks, comments и т.д.):
- Автоматическое кэширование и инвалидация
- `queryKey` — массив идентификаторов кэша
- `useMutation` + `onSuccess` → `queryClient.invalidateQueries` для обновления

---

## 18. API-клиент (Frontend)

### Axios-инстанс (`api/client.js`)

```js
baseURL: VITE_API_BASE || '/kanban/api'
withCredentials: true   // для cookie-аутентификации
```

**Интерцептор ответа:**
1. Если статус 401 и не `/auth/refresh` → вызывает `POST /auth/refresh`
2. Если обновление успешно → повторяет оригинальный запрос
3. Если нет → разлогинивает пользователя

### Группы API-методов (`api/resources.js`)

| Группа | Методы |
|--------|--------|
| `authApi` | `login`, `register`, `check`, `refresh`, `logout` |
| `usersApi` | `me`, `updateName`, `updatePassword`, `uploadAvatar` |
| `groupsApi` | `list`, `get`, `create`, `update`, `remove` |
| `membersApi` | `list`, `add`, `updateRole`, `remove` |
| `invitationsApi` | `listForGroup`, `createEmail`, `createLink`, `cancel`, `my`, `respond`, `join` |
| `boardsApi` | `all`, `listForGroup`, `get`, `create`, `update`, `remove` |
| `boardUsersApi` | `list`, `add`, `remove` |
| `columnsApi` | `list`, `create`, `update`, `move`, `remove` |
| `tasksApi` | `list`, `get`, `listByColumn`, `mine`, `create`, `update`, `remove` |
| `commentsApi` | `list`, `create`, `update`, `remove` |
| `historyApi` | `list` |
| `attachmentsApi` | `list`, `upload`, `remove` |

---

## 19. Схема базы данных

**Файл:** `src/Database/CreateDBandTables.sql`  
**СУБД:** MySQL 8.0+  
**Кодировка:** `utf8mb4_unicode_ci`

### Таблицы и ключевые связи

```
users (id PK)
  ↑
  │ created_by FK
boards (id PK, group_id FK → groups_team.id)
  │                      ↑
  │              groups_team (id PK)
  │                      ↑
  │              group_members (group_id FK, user_id FK → users.id) [role]
  │
  ├── board_users (board_id FK, user_id FK → users.id)
  │
  └── columns (id PK, board_id FK)
        └── tasks (id PK, column_id FK, assignee_id FK → users.id)
              ├── comments (id PK, task_id FK, user_id FK)
              ├── attachments (id PK, task_id FK, user_id FK)
              └── task_history (id PK, task_id FK, user_id FK)

invitations (id PK, group_id FK → groups_team.id, created_by FK → users.id)
```

### Индексы

| Таблица | Индекс |
|---------|--------|
| `users` | `UNIQUE(email)` |
| `group_members` | `INDEX(user_id)` |
| `boards` | `INDEX(group_id)` |
| `board_users` | `INDEX(user_id)` |
| `columns` | `INDEX(board_id)` |
| `tasks` | `INDEX(column_id)`, `INDEX(assignee_id)` |
| `comments` | `INDEX(task_id)` |
| `invitations` | `UNIQUE(invite_token)` |

---

## 20. Жизненный цикл запроса

### Аутентифицированный запрос (пример: GET /boards/)

```
Browser
  │ HTTP GET /kanban/api/boards/
  │ Cookie: accessTokenKanban=<jwt>
  ▼
Spring DispatcherServlet
  │
  ▼
JwtAuthenticationFilter
  │ 1. Извлекает JWT из cookie
  │ 2. Вызывает JwtService.isTokenValid()
  │ 3. Загружает CustomUserDetails по userId
  │ 4. Устанавливает Authentication в SecurityContext
  ▼
Spring Security FilterChain
  │ Проверяет isAuthenticated() → true
  ▼
BoardController.getUserBoards(@AuthenticationPrincipal principal)
  │
  ▼
BoardService.getUserBoards(principal)
  │ 1. Получает userId из principal
  │ 2. boardRepository.findAllByBoardUsers_User_Id(userId)
  │ 3. Маппит List<Board> → List<BoardDto>
  ▼
BoardMapper.toDto(board) × N
  ▼
HTTP 200 OK
Content-Type: application/json
[{ "id": 1, "title": "...", ... }, ...]
```

### Обновление задачи (с аудитом)

```
PUT /boards/{boardId}/tasks/{taskId}
  ▼
TaskController.updateTask()
  ▼
TaskService.updateTask()
  │ 1. Проверяет доступ пользователя к доске
  │ 2. Загружает существующую Task
  │ 3. Сравнивает поля (для определения изменений)
  │ 4. Обновляет поля задачи
  │ 5. taskRepository.save(task)
  │ 6. applicationEventPublisher.publishEvent(new TaskChangeEvent(task, changes, user))
  ▼
TaskHistoryListener.onTaskChange(event)
  │ Для каждого изменённого поля:
  │ taskHistoryRepository.save(new TaskHistory(...))
  ▼
TaskMapper.toDto(task) → TaskDto
  ▼
HTTP 200 OK
```

---

## 21. Ключевые сценарии работы

### Сценарий 1: Регистрация нового пользователя

1. Пользователь заполняет форму `/register`
2. `POST /auth/registration` с `{email, name, password}`
3. Бэкенд: проверяет уникальность email → BCrypt-хэширует пароль → сохраняет User → генерирует JWT → устанавливает cookies
4. Фронт: `authStore.setUser(user)`, редирект на `/dashboard`

### Сценарий 2: Создание группы и приглашение участника

1. Пользователь (admin) создаёт группу: `POST /groupteams/`
2. Открывает форму приглашения: `POST /invitations/group/{id}/email` с `{email, expiresInDays: 7}`
3. Система генерирует UUID-токен, создаёт запись Invitation (status=PENDING)
4. Приглашённый видит запись в `/invitations/my` (если зарегистрирован)
5. Нажимает «Принять»: `POST /invitations/{id}/respond` с `{accept: true}`
6. Система меняет статус PENDING → ACCEPTED, создаёт GroupMember (role=member)

### Сценарий 3: Работа с Kanban-доской

1. Открыть доску `/boards/{id}` — фронт запрашивает колонки и задачи параллельно
2. Drag-and-drop задачи:
   - `useSortable` (dnd-kit) фиксирует событие drop
   - Оптимистичное обновление: UI меняется немедленно
   - `PUT /boards/{boardId}/tasks/{taskId}` с новым `columnId` и `position`
   - При ошибке — откат через React Query
3. Создание задачи: форма в колонке → `POST /boards/{boardId}/tasks/`

### Сценарий 4: Просмотр истории задачи

1. Открыть модальное окно задачи → вкладка «История»
2. `GET /boards/{boardId}/tasks/{taskId}/history`
3. Отображается хронологический список изменений: кто, когда, что изменил (старое → новое значение)

---

*Документ описывает систему в полном объёме по состоянию на май 2026 года.*
