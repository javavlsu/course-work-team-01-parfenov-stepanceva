CREATE DATABASE Canban01
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
USE Canban01;

-- 1. Создание таблицы Пользователь
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Создание таблицы Группы (Новая сущность)
CREATE TABLE groups_team (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_id INT NOT NULL, -- Создатель группы (Супер-админ группы)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Создание таблицы Участники_Группы (Связь Пользователь-Группа + Роль в группе)
CREATE TABLE group_members (
    group_id INT,
    user_id INT,
    role VARCHAR(50) DEFAULT 'member', -- 'owner', 'admin', 'member'
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES groups_team(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Создание таблицы Доска (Изменена привязка к группе)
CREATE TABLE boards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL, -- Доска теперь обязательно принадлежит группе
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by INT, -- Кто технически нажал кнопку "создать"
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups_team(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 5. Создание таблицы Доступ_к_Доске (Связь многие-ко-многим для точечной настройки)
-- Это позволяет админу группы/доски давать или закрывать доступ к конкретной доске внутри группы
CREATE TABLE board_users (
    board_id INT,
    user_id INT,
    role VARCHAR(50) DEFAULT 'viewer', -- 'admin', 'editor', 'viewer' (роль именно на этой доске)
    PRIMARY KEY (board_id, user_id),
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6. Создание таблицы Колонка (Без изменений)
CREATE TABLE columns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    board_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    position INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(board_id, position),
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Создание таблицы Задача (Без изменений)
CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    column_id INT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assignee_id INT NULL,
    deadline TIMESTAMP NULL,
    priority VARCHAR(20) DEFAULT 'medium', -- Рекомендуемое улучшение (см. ниже)
    status VARCHAR(50) DEFAULT 'todo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (column_id) REFERENCES columns(id) ON DELETE CASCADE,
    FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 8. Создание таблицы Комментарий (Без изменений)
CREATE TABLE comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT,
    user_id INT,
    text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. Создание таблицы Вложение (Без изменений)
CREATE TABLE attachments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT,
    user_id INT,
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. Создание таблицы История задачи (Без изменений)
CREATE TABLE task_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT,
    user_id INT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_attribute VARCHAR(255) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Создание таблицы Приглашения 
-- Обновлено: теперь приглашения могут быть и в группу
CREATE TABLE invitations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL, -- Приглашаем в первую очередь в группу
    board_id INT DEFAULT NULL, -- Опционально: сразу даем доступ к конкретной доске
    email VARCHAR(255) NOT NULL, -- Кого приглашаем
    invite_token VARCHAR(255) UNIQUE NOT NULL,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (group_id) REFERENCES groups_team(id) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;