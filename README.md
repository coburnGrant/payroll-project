# SDEV-268 Final Project - Payroll Program

A comprehensive payroll management system built with Java and MySQL, featuring role-based access control, time entry management, payroll processing, and reporting capabilities.

## Table of Contents
- [Setup Instructions](#setup-instructions)
- [Documentation](#documentation)
   - [Technologies Used](#technologies-used)
   - [Database Design](#database-design)
   - [Main Actors](#main-actors)
   - [Project Timeline](#project-timeline)
   - [Testing Procedures](#testing-procedures)
   - [Security Measures](#security-measures)
   - [Features and Functionality](#features-and-functionality)
      - [Login Screen](#login-screen)
      - [Admin Dashboard](#admin-dashboard)
         - [Employee Management](#employee-management)
         - [Payroll Processing](#payroll-processing)
         - [Report Generation](#report-generation)
      - [Employee Dashboard](#employee-dashboard)
         - [Initial Login](#initial-login)
         - [Time Entry](#time-entry)
         - [Time Sheet](#time-sheet)
         - [Paychecks](#paychecks)

## Setup Instructions

1. **Database Setup**
   - Install MySQL if not already installed
   - Create a new database named `payroll_system`
   ```sql
   CREATE DATABASE payroll_system;
   ```
   - Run the schema file located at `src/main/resources/schema.sql`
   
   ```bash
   mysql -u your_username -p payroll_system < src/main/resources/schema.sql
   ```

2. **Application Setup**
   - Clone the repository
   - Configure database connection in `src/main/java/grant/coburn/util/DBProperties.java`
   - Build the project:
   ```bash
   mvn clean install
   ```
   - Run the application:
   ```bash
   mvn javafx:run
   ```

## Documentation

### Technologies Used
- Maven for dependency management
- JavaFX for the user interface
- MySQL for data persistence
- BCrypt for password encrypting
- iText for PDF generation
- OpenCSV for CSV handling

### Database Design
- Application runs using a MySQL database
   - The schema for this database can be found [here](https://github.com/coburnGrant/payroll-project/blob/8e822e701e9004ed58198e047172cf54cd0f781c/src/main/resources/schema.sql)
- MySQL database with tables for:
  - Employees (personal and payroll information)
  - Time entries (work hours and PTO tracking)
  - Payroll records (processed payroll data)
  - User credentials and roles

### Main Actors

#### Data Access Objects (DAOs)

This project utilizes the Data Access Object (DAO) design paradigm to handle CRUD operations

- EmployeeDAO
- UserDAO
- TimeEntryDAO
- PayrollRecordDAO

#### Payroll Calculations
- PayrollCalculator
- PayrollProcessor

### Project Timeline

#### Week 1-2: Setup and Basic Structure
- Project planning
- Database schema design
- Basic UI implementation
- Authentication system

#### Week 3-4: Core Features
- Time entry system
- Employee management
- Basic payroll processing

#### Week 5-6: Advanced Features
- Tax calculations
- Benefits management
- Report generation

#### Week 7-8: Testing and Refinement
- System testing
- Bug fixes
- Documentation
- User acceptance testing

### Testing Procedures

#### JUnit Tests
- Unit tests have been created to test and verify the functionality of critical functions of the application. Such as password encryption, data validation, and payroll calculations.

#### GitHub Actions
- This repository is configured to automatically run unit tests on every commit and pull request to the `main` branch.
- This ensures continuous integration and helps catch bugs early in the development cycle.
- Workflow runs can be found here on [GitHub](https://github.com/coburnGrant/payroll-project/actions)

### Security Measures
- Role-based access control (Admin/Employee)
- Password hashing using BCrypt
- Prepared statements to prevent SQL injection
- Input validation and sanitization
- Secure session management
- Encrypted database connection

### Features and Functionality

#### Login Screen

![login screen](./documentation/screenshots/login%20screen.png)
- Login by selecting the user type and entering user ID and password

#### Admin Dashboard

![admin dashboard](./documentation/screenshots/admin/admin%20dashboard.png)
- Once logging in as an Admin, the admin dashboard will be displayed where the admin user can:
   1. Mange Employees
   2. Process Payroll
   3. Generate Reports

##### Employee Management 

![employee management screen](./documentation/screenshots/admin/employee%20management.png)
- From the employee management screen an admin can:
   1. View all employees
   2. Add new employees
      - ![add employee screen](./documentation/screenshots/admin/add%20employee.png)
      - After adding employee information and creating an employee, a popup with a auto-generated password will be displayed with the employee's login credentials
         - ![login creds](./documentation/screenshots/admin/login%20credentials.png)
   3. Edit existing employee records
   4. View employee time sheet
   5. Search for employees

##### Payroll Processing

![payroll processing screen](./documentation/screenshots/admin/payroll%20processing.png)
- From the payroll processing screen, an Admin can select a pay period and process payroll.

![payroll processed](./documentation/screenshots/admin/payroll%20processed.png)
- After processing payroll, an Admin can view all paychecks that were generated for all employees. 
   - Selecting a row will allow the admin to:
      - Delete the entry
      - View the paycheck
         - ![paycheck details](./documentation/screenshots/admin/paycheck%20details.png)

##### Report Generation
- After processing payroll, an admin can generate PDF and CSV reports for any given pay period

![report generation screen](./documentation/screenshots/admin/generate%20reports.png)
- To generate reports:
   1. Select the pay period you wish to report on
   2. Select the report types you wish to generate (PDF or CSV)
   3. Click "Generate Reports"
   4. Select the output location that for the reports
   5. After reports are generated, a popup will be issued with the status of the reports
      - ![report status](./documentation/screenshots/admin/report%20generation%20success.png)
- Sample reports can be found here:
   - [PDF](./documentation/sample%20reports/payroll_report_2025-02-08_to_2025-03-08.pdf)
   - [CSV](./documentation/sample%20reports/payroll_report_2025-02-08_to_2025-03-08.csv)

#### Employee Dashboard

##### Initial Login
- Upon the initial login of an employee, the employee must change their password to a more secure password than the auto-generated password created by the admin.
- ![change password screen](./documentation/screenshots/employee/change%20password.png)
   - If passwords do not match the requirements, popups will be issued
      - ![upper case char required](./documentation/screenshots/employee/password%20error%20-%20uppercase.png)
      - ![special char required](./documentation/screenshots/employee/password%20error%20-%20special%20char.png)

##### Dashboard 

After changing password successfully, the employee dashboard will be displayed 

For salaried employees:
- ![salaried employee dashboard](./documentation/screenshots/employee/employee%20dashboard%20-%20salary.png)

For hourly employees:
- ![hourly employee dashboard](./documentation/screenshots/employee/employee%20dashboard%20-%20hourly.png)

##### Time Entry

For salaried employees:
- Salaried employees will not be able to enter regular time since they are on salary
   - ![salary time entry screen](./documentation/screenshots/employee/employee%20time%20entry%20-%20salary.png)
- However, they will be able to enter PTO
   - ![pto time entry](./documentation/screenshots/employee/employee%20time%20entry%20-%20salary%20pto.png)

For hourly employees:
- ![time entry screen](./documentation/screenshots/employee/employee%20time%20entry%20-%20hourly.png)
   - A preview of how much they will get paid for this entry is displayed below the entry textbox. This is just a preview and calculations like deductions may not be completely accurate due to factors like pay period length and deductions.

##### Time Sheet
![time sheet](./documentation/screenshots/employee/employee%20time%20sheet.png)
- Employees are able to view a record of all their time entries. 
- Employees will be allowed to edit their entries until payroll is processed. Entries will be "locked" after processing payroll and no longer editable.

##### Paychecks
![paycheck screen](./documentation/screenshots/employee/employee%20paychecks.png)
- Employees will be able to view all of their generated paychecks

- An employee can click on a paycheck to view more details
   - ![paycheck details](./documentation/screenshots/employee/paycheck%20details.png)