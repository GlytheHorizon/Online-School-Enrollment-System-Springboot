CREATE DATABASE IF NOT EXISTS school_enrollment;
USE school_enrollment;

CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE DEFAULT (CURDATE()),
    active TINYINT(1) DEFAULT 1
);

CREATE TABLE IF NOT EXISTS courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    units INT NOT NULL,
    tuition_per_unit DECIMAL(10,2) NOT NULL,
    active TINYINT(1) DEFAULT 1
);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE DEFAULT (CURDATE()),
    status VARCHAR(20) DEFAULT 'Enrolled',
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_date DATE DEFAULT (CURDATE()),
    reference_number VARCHAR(50),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS enrollment_audit (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    course_id INT NOT NULL,
    action VARCHAR(20) NOT NULL,
    action_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

INSERT INTO courses (course_code, course_name, units, tuition_per_unit) VALUES
('CS101', 'Introduction to Programming', 3, 1500.00),
('CS102', 'Data Structures', 3, 1800.00),
('CS201', 'Database Systems', 3, 2000.00),
('MATH101', 'Calculus I', 3, 1200.00),
('ENG101', 'English Composition', 3, 1000.00);
