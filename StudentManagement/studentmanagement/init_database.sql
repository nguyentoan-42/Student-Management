CREATE DATABASE student_management;
GO
USE student_management;
GO

-- Bảng tài khoản
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Bảng sinh viên
CREATE TABLE students (
    student_id VARCHAR(50) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    department NVARCHAR(100) NOT NULL,
    class_name VARCHAR(50) NOT NULL,
    score_1 FLOAT,
    score_2 FLOAT,
    score_3 FLOAT,
    score_4 FLOAT,
    score_5 FLOAT,
    score_6 FLOAT,
    score_7 FLOAT,
    score_8 FLOAT,
    score_9 FLOAT,
    score_10 FLOAT,
    gpa FLOAT
);

-- Tài khoản admin gốc
INSERT INTO users VALUES ('admin', 'admin123', 'ADMIN');


SELECT * FROM users;
SELECT * FROM students;

USE master;
GO
DROP DATABASE student_management;
GO