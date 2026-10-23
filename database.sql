-- ========================================================
-- LABORATORY MANAGEMENT SYSTEM DATABASE SCHEMA & SEED DATA
-- Database: laboratory_management
-- Technology: MySQL 8.0+
-- ========================================================

DROP DATABASE IF EXISTS laboratory_management;
CREATE DATABASE laboratory_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE laboratory_management;

-- --------------------------------------------------------
-- Table: USERS
-- Stores core authentication credentials and roles
-- --------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Stored as SHA-256 hash
    role ENUM('ADMIN', 'FACULTY', 'STUDENT') NOT NULL,
    department VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: LABORATORIES
-- Stores laboratory facility details, capacity, and equipment
-- --------------------------------------------------------
CREATE TABLE laboratories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lab_name VARCHAR(100) NOT NULL,
    lab_code VARCHAR(50) NOT NULL UNIQUE,
    room_number VARCHAR(50) NOT NULL,
    department VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    equipment_details TEXT,
    description TEXT
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: STUDENTS
-- Stores student academic details linked to users table
-- --------------------------------------------------------
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    semester VARCHAR(20) NOT NULL,
    section VARCHAR(20),
    department VARCHAR(100) NOT NULL,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: FACULTY
-- Stores faculty profile details linked to users table
-- --------------------------------------------------------
CREATE TABLE faculty (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    CONSTRAINT fk_faculty_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: MARKS
-- Stores laboratory evaluations, marks scored, and max marks
-- --------------------------------------------------------
CREATE TABLE marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    faculty_id INT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    assessment VARCHAR(100) NOT NULL,
    marks DECIMAL(5,2) NOT NULL,
    max_marks DECIMAL(5,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id) 
        REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_faculty FOREIGN KEY (faculty_id) 
        REFERENCES faculty(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: NOTICES
-- Stores announcements published by faculty
-- --------------------------------------------------------
CREATE TABLE notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    faculty_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notices_faculty FOREIGN KEY (faculty_id) 
        REFERENCES faculty(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: TASKS
-- Stores lab assignments, problem statements, and submission dates
-- --------------------------------------------------------
CREATE TABLE tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    faculty_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    submission_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_faculty FOREIGN KEY (faculty_id) 
        REFERENCES faculty(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: LAB_EXAMS
-- Stores scheduled laboratory examinations and lab venue
-- --------------------------------------------------------
CREATE TABLE lab_exams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    faculty_id INT NOT NULL,
    laboratory_id INT NOT NULL,
    exam_name VARCHAR(150) NOT NULL,
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_exams_faculty FOREIGN KEY (faculty_id) 
        REFERENCES faculty(id) ON DELETE CASCADE,
    CONSTRAINT fk_exams_laboratory FOREIGN KEY (laboratory_id) 
        REFERENCES laboratories(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- --------------------------------------------------------
-- Table: ATTENDANCE
-- Stores login-based student attendance records
-- Enforces 1 attendance record per student per calendar day
-- --------------------------------------------------------
CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    date DATE NOT NULL,
    login_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'PRESENT',
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) 
        REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_date UNIQUE (student_id, date)
) ENGINE=InnoDB;

-- ========================================================
-- SAMPLE SEED DATA
-- All passwords are hashed using SHA-256:
-- admin123   -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- faculty123 -> 27041f5856c7387a997252694afb048d1aa939228ffcdbd6285b979b8da20e7a
-- student123 -> 703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b
-- ========================================================

-- Insert Users
INSERT INTO users (id, name, email, username, password, role, department, phone) VALUES
(1, 'System Administrator', 'admin@college.edu', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'Information Technology', '9876543210'),
(2, 'Dr. Aris Thorne', 'faculty1@college.edu', 'faculty1', '27041f5856c7387a997252694afb048d1aa939228ffcdbd6285b979b8da20e7a', 'FACULTY', 'Computer Science', '9876543211'),
(3, 'Prof. Elena Rostova', 'faculty2@college.edu', 'faculty2', '27041f5856c7387a997252694afb048d1aa939228ffcdbd6285b979b8da20e7a', 'FACULTY', 'Information Technology', '9876543212'),
(4, 'Aarav Sharma', 'student1@college.edu', 'student1', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT', 'Computer Science', '9876543221'),
(5, 'Diya Patel', 'student2@college.edu', 'student2', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT', 'Information Technology', '9876543222'),
(6, 'Rohan Verma', 'student3@college.edu', 'student3', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', 'STUDENT', 'Computer Science', '9876543223');

-- Insert Faculty Profiles
INSERT INTO faculty (id, user_id, employee_id, department, designation) VALUES
(1, 2, 'EMP-CS-101', 'Computer Science', 'Associate Professor'),
(2, 3, 'EMP-IT-102', 'Information Technology', 'Assistant Professor');

-- Insert Student Profiles
INSERT INTO students (id, user_id, roll_number, semester, section, department) VALUES
(1, 4, 'CS2026-001', 'Semester 5', 'Section A', 'Computer Science'),
(2, 5, 'IT2026-042', 'Semester 5', 'Section B', 'Information Technology'),
(3, 6, 'CS2026-088', 'Semester 5', 'Section A', 'Computer Science');

-- Insert Laboratories
INSERT INTO laboratories (id, lab_name, lab_code, room_number, department, capacity, equipment_details, description) VALUES
(1, 'Advanced Computing & AI Lab', 'LAB-CS-301', 'Block B - Room 301', 'Computer Science', 45, '45x Intel Core i7 13th Gen Workstations, 32GB RAM, NVIDIA RTX 4070, Gigabit LAN, High-speed Fiber', 'Dedicated laboratory for Artificial Intelligence, Deep Learning, and Parallel Computing practicals.'),
(2, 'Software Engineering & Web Systems Lab', 'LAB-IT-204', 'Block C - Room 204', 'Information Technology', 40, '40x Intel Core i5 12th Gen PCs, Dual Monitors, Apache Tomcat Servers, Cisco Managed Switches', 'Focused on Full-Stack Java Development, Database Administration, and Web Technologies coursework.');

-- Insert Notices
INSERT INTO notices (id, faculty_id, title, description, created_at) VALUES
(1, 1, 'Lab Safety & Clean Code Guidelines 2026', 'All students must adhere to the departmental laboratory protocols. Ensure your code repositories are committed before the end of each session.', NOW() - INTERVAL 3 DAY),
(2, 2, 'Mid-Term Lab Practical Schedule Announced', 'The mid-term practical evaluation for Database Management and Web Technologies is scheduled for next week. Review experiment manuals 1 through 6.', NOW() - INTERVAL 1 DAY);

-- Insert Tasks (Assignments & Problem Statements)
INSERT INTO tasks (id, faculty_id, title, description, submission_date, created_at) VALUES
(1, 1, 'Implementation of MVC with JDBC', 'Develop a standalone Java application demonstrating MVC architecture and JDBC connectivity with prepared statements.', CURRENT_DATE + INTERVAL 5 DAY, NOW() - INTERVAL 2 DAY),
(2, 2, 'Database Normalization & Query Tuning Task', 'Submit the BCNF normalization report and execution plan comparison for complex laboratory queries.', CURRENT_DATE + INTERVAL 8 DAY, NOW() - INTERVAL 1 DAY);

-- Insert Lab Exams
INSERT INTO lab_exams (id, faculty_id, laboratory_id, exam_name, exam_date, start_time, end_time) VALUES
(1, 1, 1, 'Advanced Data Structures Practical Exam', CURRENT_DATE + INTERVAL 10 DAY, '10:00:00', '13:00:00'),
(2, 2, 2, 'Web Systems & Servlet Viva Voce', CURRENT_DATE + INTERVAL 12 DAY, '14:00:00', '17:00:00');

-- Insert Sample Marks
INSERT INTO marks (id, student_id, faculty_id, subject, assessment, marks, max_marks) VALUES
(1, 1, 1, 'Data Structures Lab', 'Lab Experiment 1 - Linked List Ops', 28.50, 30.00),
(2, 1, 1, 'Data Structures Lab', 'Lab Experiment 2 - Binary Search Trees', 27.00, 30.00),
(3, 2, 2, 'Web Technologies Lab', 'Lab Assignment 1 - Servlet Filter Auth', 29.00, 30.00),
(4, 3, 1, 'Data Structures Lab', 'Lab Experiment 1 - Linked List Ops', 25.00, 30.00);

-- Insert Sample Attendance
INSERT INTO attendance (id, student_id, date, login_time, status) VALUES
(1, 1, CURRENT_DATE - INTERVAL 1 DAY, '09:14:22', 'PRESENT'),
(2, 2, CURRENT_DATE - INTERVAL 1 DAY, '09:18:05', 'PRESENT');
