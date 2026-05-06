-- TaskFlow Database Setup
-- Run this in MySQL Workbench or MySQL CLI before starting the app

CREATE DATABASE IF NOT EXISTS taskflowdb;
USE taskflowdb;

-- USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- PROJECTS TABLE
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    status ENUM('ACTIVE', 'ON_HOLD', 'COMPLETED') NOT NULL DEFAULT 'ACTIVE',
    owner_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- TASKS TABLE
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    description TEXT,
    status ENUM('TODO', 'IN_PROGRESS', 'DONE') NOT NULL DEFAULT 'TODO',
    priority ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM',
    due_date DATE,
    project_id BIGINT NOT NULL,
    assigned_to_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_assignee FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL
);
