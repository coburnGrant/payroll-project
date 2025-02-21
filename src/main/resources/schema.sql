CREATE DATABASE IF NOT EXISTS payroll_system;
USE payroll_system;

CREATE TABLE users (
    user_id VARCHAR(10) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('ADMIN', 'EMPLOYEE') NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE employees (
    employee_id VARCHAR(10) PRIMARY KEY,
    user_id VARCHAR(10) UNIQUE,
    department VARCHAR(50) NOT NULL,
    job_title VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    sur_name VARCHAR(50),
    status ENUM('ACTIVE', 'TERMINATED') NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE') NOT NULL,
    pay_type ENUM('SALARY', 'HOURLY') NOT NULL,
    company_email VARCHAR(100) UNIQUE NOT NULL,
    address_line1 VARCHAR(100) NOT NULL,
    address_line2 VARCHAR(100),
    city VARCHAR(50) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip VARCHAR(10) NOT NULL,
    picture_path VARCHAR(255),
    hire_date DATE NOT NULL,
    base_salary DECIMAL(10,2),
    medical_coverage ENUM('SINGLE', 'FAMILY'),
    dependents_count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE time_entries (
    entry_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(10) NOT NULL,
    work_date DATE NOT NULL,
    hours_worked DECIMAL(4,2) NOT NULL,
    is_pto BOOLEAN DEFAULT FALSE,
    is_locked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

CREATE TABLE payroll_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(10) NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    gross_pay DECIMAL(10,2) NOT NULL,
    net_pay DECIMAL(10,2) NOT NULL,
    medical_deduction DECIMAL(10,2),
    dependent_stipend DECIMAL(10,2),
    state_tax DECIMAL(10,2),
    federal_tax DECIMAL(10,2),
    social_security_tax DECIMAL(10,2),
    medicare_tax DECIMAL(10,2),
    employer_social_security DECIMAL(10,2),
    employer_medicare DECIMAL(10,2),
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Insert default admin user
INSERT INTO users (user_id, password, user_type, email)
VALUES ('HR0001', '$2a$10$xP3/fKqwN2mP5YhZ.zXkPu4q.V.rz/NDXwJwK8ZUiVBn.U7rHk1Hy', 'ADMIN', 'admin@abccompany.com'); 