# SDEV-268 Final Project - Payroll Program

A comprehensive payroll management system built with Java and MySQL, featuring role-based access control, time entry management, payroll processing, and reporting capabilities.

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