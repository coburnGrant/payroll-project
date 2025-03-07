package grant.coburn.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import grant.coburn.model.Employee;
import grant.coburn.model.User;
import grant.coburn.util.DatabaseUtil;

public class EmployeeDAO {
    public class EmployeeCredentials {
        public final String userId;
        public final String password;
        public final String fullName;
    
        public EmployeeCredentials(String userId, String password, String fullName) {
            this.userId = userId;
            this.password = password;
            this.fullName = fullName;
        }
    } 

    private final DatabaseUtil dbUtil;
    
    public static final EmployeeDAO shared = new EmployeeDAO(DatabaseUtil.shared);

    public EmployeeDAO(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    /**
     * Get all active employees.
     * @return List of all employees
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE status = 'ACTIVE'";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(createEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return employees;
    }

    /**
     * Get an employee by their ID.
     * @param employeeId The ID of the employee to retrieve
     * @return The employee if found, null otherwise
     */
    public Employee getEmployeeById(String employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ? AND status = 'ACTIVE'";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Employee getEmployee(String employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (
            Connection conn = dbUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateSecurePassword(boolean isTestEmployee) {
        if (isTestEmployee) {
            return "password123";
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }

    public EmployeeCredentials createEmployee(Employee employee) {
        return createEmployee(employee, false);
    }

    public EmployeeCredentials createEmployee(Employee employee, boolean isTestEmployee) {
        try (Connection conn = dbUtil.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO employees (employee_id, department, job_title, first_name, last_name, " +
                        "status, date_of_birth, hire_date, pay_type, base_salary, medical_coverage, dependents_count, " +
                        "company_email, gender, address_line1, address_line2, city, state, zip, picture_path) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                setEmployeeParameters(pstmt, employee);
                pstmt.executeUpdate();

                String tempPassword = generateSecurePassword(isTestEmployee);
                User newUser = new User(
                    employee.getEmployeeId(),
                    tempPassword,
                    User.UserType.EMPLOYEE,
                    employee.getCompanyEmail()
                );
                newUser.setEmployeeId(employee.getEmployeeId());

                try {
                    boolean userCreated = UserDAO.shared.createUser(newUser, conn);
                    if (!userCreated) {
                        conn.rollback();
                        return null;
                    }
                    conn.commit();
                    return new EmployeeCredentials(
                        employee.getEmployeeId(),
                        tempPassword,
                        employee.getFullName()
                    );
                } catch (SQLException e) {
                    conn.rollback();
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET department = ?, job_title = ?, first_name = ?, " +
                    "last_name = ?, status = ?, date_of_birth = ?, hire_date = ?, pay_type = ?, " +
                    "base_salary = ?, medical_coverage = ?, dependents_count = ?, " +
                    "company_email = ?, gender = ?, address_line1 = ?, address_line2 = ?, " +
                    "city = ?, state = ?, zip = ?, picture_path = ? " +
                    "WHERE employee_id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // First 19 parameters
            pstmt.setString(1, employee.getDepartment());
            pstmt.setString(2, employee.getJobTitle());
            pstmt.setString(3, employee.getFirstName());
            pstmt.setString(4, employee.getLastName());
            pstmt.setInt(5, employee.getStatus() == Employee.Status.ACTIVE ? 1 : 2);
            pstmt.setDate(6, Date.valueOf(employee.getDateOfBirth()));
            pstmt.setDate(7, Date.valueOf(employee.getHireDate()));
            pstmt.setInt(8, employee.getPayType() == Employee.PayType.SALARY ? 1 : 2);
            pstmt.setDouble(9, employee.getBaseSalary());
            pstmt.setInt(10, employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE ? 1 : 2);
            pstmt.setInt(11, employee.getDependentsCount());
            pstmt.setString(12, employee.getCompanyEmail());
            pstmt.setInt(13, employee.getGender() == Employee.Gender.MALE ? 1 : 2);
            pstmt.setString(14, employee.getAddressLine1());
            pstmt.setString(15, employee.getAddressLine2());
            pstmt.setString(16, employee.getCity());
            pstmt.setString(17, employee.getState());
            pstmt.setString(18, employee.getZip());
            pstmt.setString(19, employee.getPicturePath());
            
            // WHERE clause parameter
            pstmt.setString(20, employee.getEmployeeId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(String employeeId, boolean hardDelete) {
        if (hardDelete) {
            return hardDeleteEmployee(employeeId);
        } else {
            return softDeleteEmployee(employeeId);
        }
    }

    private boolean softDeleteEmployee(String employeeId) {
        String updateEmployeeSql = "UPDATE employees SET status = 2 WHERE employee_id = ?";

        try(
            Connection conn = dbUtil.getConnection();
            PreparedStatement updateEmployeePstmt = conn.prepareStatement(updateEmployeeSql)
        ) {
            conn.setAutoCommit(false);

            try {
                updateEmployeePstmt.setString(1, employeeId);
                int employeeRowsAffected = updateEmployeePstmt.executeUpdate();

                if (employeeRowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hardDeleteEmployee(String employeeId) {
        // Delete related records first
        String deletePayrollRecordsSql = "DELETE FROM payroll_records WHERE employee_id = ?";
        String deleteTimeEntriesSql = "DELETE FROM time_entries WHERE employee_id = ?";
        String deleteUserSql = "DELETE FROM users WHERE employee_id = ?";
        String deleteEmployeeSql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = dbUtil.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete payroll records
                try (PreparedStatement pstmt = conn.prepareStatement(deletePayrollRecordsSql)) {
                    pstmt.setString(1, employeeId);
                    pstmt.executeUpdate();
                }

                // Delete time entries
                try (PreparedStatement pstmt = conn.prepareStatement(deleteTimeEntriesSql)) {
                    pstmt.setString(1, employeeId);
                    pstmt.executeUpdate();
                }

                // Delete user account
                try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
                    pstmt.setString(1, employeeId);
                    pstmt.executeUpdate();
                }

                // Finally, delete the employee
                try (PreparedStatement pstmt = conn.prepareStatement(deleteEmployeeSql)) {
                    pstmt.setString(1, employeeId);
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // For backward compatibility
    public boolean deleteEmployee(String employeeId) {
        return deleteEmployee(employeeId, false); // Default to soft delete
    }

    private Employee createEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee(
            rs.getString("employee_id"),
            rs.getString("department"),
            rs.getString("job_title"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            Employee.Status.valueOf(rs.getString("status")),
            rs.getDate("date_of_birth").toLocalDate(),
            rs.getDate("hire_date").toLocalDate(),
            Employee.PayType.valueOf(rs.getString("pay_type")),
            rs.getDouble("base_salary"),
            Employee.MedicalCoverage.valueOf(rs.getString("medical_coverage")),
            rs.getInt("dependents_count")
        );

        // Set additional fields
        employee.setCompanyEmail(rs.getString("company_email"));
        employee.setGender(Employee.Gender.valueOf(rs.getString("gender")));
        employee.setAddressLine1(rs.getString("address_line1"));
        employee.setAddressLine2(rs.getString("address_line2"));
        employee.setCity(rs.getString("city"));
        employee.setState(rs.getString("state"));
        employee.setZip(rs.getString("zip"));
        employee.setPicturePath(rs.getString("picture_path"));

        return employee;
    }

    private void setEmployeeParameters(PreparedStatement pstmt, Employee employee) throws SQLException {
        pstmt.setString(1, employee.getEmployeeId());
        pstmt.setString(2, employee.getDepartment());
        pstmt.setString(3, employee.getJobTitle());
        pstmt.setString(4, employee.getFirstName());
        pstmt.setString(5, employee.getLastName());
        pstmt.setInt(6, employee.getStatus() == Employee.Status.ACTIVE ? 1 : 2);
        pstmt.setDate(7, Date.valueOf(employee.getDateOfBirth()));
        pstmt.setDate(8, Date.valueOf(employee.getHireDate()));
        pstmt.setInt(9, employee.getPayType() == Employee.PayType.SALARY ? 1 : 2);
        pstmt.setDouble(10, employee.getBaseSalary());
        pstmt.setInt(11, employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE ? 1 : 2);
        pstmt.setInt(12, employee.getDependentsCount());
        pstmt.setString(13, employee.getCompanyEmail());
        pstmt.setInt(14, employee.getGender() == Employee.Gender.MALE ? 1 : 2);
        pstmt.setString(15, employee.getAddressLine1());
        pstmt.setString(16, employee.getAddressLine2());
        pstmt.setString(17, employee.getCity());
        pstmt.setString(18, employee.getState());
        pstmt.setString(19, employee.getZip());
        pstmt.setString(20, employee.getPicturePath());
    }

    private Employee[] testEmployees() {
        return new Employee[] {
            // Salaried Employees
            createTestEmployee(
                "John", "Smith",
                "IT", "Senior Software Engineer",
                Employee.PayType.SALARY, 75000.00, 
                "1985-01-15", 
                Employee.Gender.MALE,
                "123 Main St",
                null,
                "Indianapolis", "IN", "46201",
                Employee.MedicalCoverage.FAMILY,
                2
            ),
            createTestEmployee(
                "Sarah", "Johnson", 
                "HR", "HR Manager", 
                Employee.PayType.SALARY, 85000.00, 
                "1980-02-01", 
                Employee.Gender.FEMALE,
                "456 Oak Ave", 
                null, 
                "Carmel", "IN", "46032", 
                Employee.MedicalCoverage.FAMILY, 
                2
            ),
            createTestEmployee(
                "Michael", "Brown", 
                "Finance", "Financial Analyst", 
                Employee.PayType.SALARY, 95000.00,
                "1988-03-10",
                Employee.Gender.MALE,
                "789 Pine Rd", null, "Fishers", "IN", "46037", 
                Employee.MedicalCoverage.SINGLE, 0
            ),
            createTestEmployee(
                "Emily", "Davis", 
                "Marketing", "Marketing Specialist", 
                Employee.PayType.SALARY, 70000.00,
                "1990-04-05", 
                Employee.Gender.FEMALE,
                "321 Maple Dr", null, 
                "Noblesville", "IN", "46060", 
                Employee.MedicalCoverage.FAMILY,
                 1
            ),
            createTestEmployee(
                "David", "Wilson", 
                "Sales", "Sales Manager", 
                Employee.PayType.SALARY, 80000.00, 
                "1982-05-20", 
                Employee.Gender.MALE,
                "654 Elm St", 
                null, 
                "Westfield", "IN", "46074", 
                Employee.MedicalCoverage.FAMILY, 
                3
            ),

            // Hourly Employees
            createTestEmployee(
                "Lisa", "Anderson", 
                "Operations", "Operations Specialist", 
                Employee.PayType.HOURLY, 25.00, 
                "1992-06-01", 
                Employee.Gender.FEMALE,
                "987 Cedar Ln", 
                null, 
                "Indianapolis", "IN", "46202", 
                Employee.MedicalCoverage.SINGLE, 
                0
            ),
            createTestEmployee(
                "Robert", "Taylor", 
                "Customer Service", "Customer Service Representative", 
                Employee.PayType.HOURLY, 22.50, 
                "1987-07-15", 
                Employee.Gender.MALE,
                "147 Birch Rd", 
                null, 
                "Carmel", "IN", "46032", 
                Employee.MedicalCoverage.FAMILY, 
                2
            ),
            createTestEmployee(
                "Jennifer", "Martinez",
                "Administration", "Administrative Assistant", 
                Employee.PayType.HOURLY, 20.00, 
                "1991-08-10", 
                Employee.Gender.FEMALE,
                "258 Spruce Ave", 
                null, 
                "Fishers", "IN", "46037", 
                Employee.MedicalCoverage.FAMILY, 
                1
            ),
            createTestEmployee(
                "Thomas", "Garcia", 
                "IT", "IT Support Specialist", 
                Employee.PayType.HOURLY, 23.00, 
                "1989-09-05", 
                Employee.Gender.MALE,
                "369 Willow St",
                null, 
                "Noblesville", "IN", "46060", 
                Employee.MedicalCoverage.SINGLE, 
                0
            ),
            createTestEmployee(
                "Michelle", "Lee", 
                "HR", "HR Coordinator", 
                Employee.PayType.HOURLY, 21.50, 
                "1993-10-01", 
                Employee.Gender.FEMALE,
                "741 Ash Dr", 
                null,
                "Westfield", "IN", "46074", 
                Employee.MedicalCoverage.FAMILY,
                1
            ),

            // Part-time Employees
            createTestEmployee(
                "James", "Rodriguez", 
                "Customer Service", "Customer Service Representative", 
                Employee.PayType.HOURLY, 18.00, 
                "1994-11-15", 
                Employee.Gender.MALE,
                "852 Poplar Rd", 
                null, 
                "Indianapolis", "IN", "46203", 
                Employee.MedicalCoverage.SINGLE, 
                0
            ),
            createTestEmployee(
                "Amanda", "White", 
                "Administration", "Administrative Assistant", 
                Employee.PayType.HOURLY, 17.50, 
                "1995-12-01", 
                Employee.Gender.FEMALE,
                "963 Cherry Ln", 
                null, 
                "Carmel", "IN", "46032", 
                Employee.MedicalCoverage.SINGLE, 
                0
            )
        };
    }

    private void setUserAsAdmin(String employeeId) {
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE users SET user_type = 'ADMIN' WHERE employee_id = ?")) {
            stmt.setString(1, employeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to set admin role: " + e.getMessage());
        }
    }

    public void insertTestEmployees() {
        int count = 0;
        for (Employee employee : testEmployees()) {
            try {
                // Create employee with user account, specifying this is a test employee
                EmployeeCredentials credentials = createEmployee(employee, true);
                if (credentials != null) {
                    System.out.println("Created employee: " + credentials.fullName + 
                                     " (ID: " + credentials.userId + 
                                     ", Password: password123" +
                                     ", Role: EMPLOYEE)");
                    count++;
                }
            } catch (Exception e) {
                System.err.println("Error creating employee " + employee.getFullName() + 
                                 ": " + e.getMessage());
            }
        }
        
        if (count > 0) {
            System.out.println("\nCreated " + count + " test employees successfully.");
            System.out.println("All employees can log in with their email address and password: password123");
        }
    }

    private Employee createTestEmployee(
        String firstName, String lastName,
        String department, String jobTitle,
        Employee.PayType payType, double baseSalary,
        String dateOfBirth, Employee.Gender gender,
        String addressLine1, String addressLine2,
        String city, String state, String zip,
        Employee.MedicalCoverage medicalCoverage, 
        int dependentsCount
    ) {
        
        String employeeId = generateEmployeeId();
        java.time.LocalDate dob = java.time.LocalDate.parse(dateOfBirth);
        java.time.LocalDate hireDate = java.time.LocalDate.now();
        
        Employee employee = new Employee(
            employeeId,
            department,
            jobTitle,
            firstName,
            lastName,
            Employee.Status.ACTIVE,
            dob,
            hireDate,
            payType,
            baseSalary,
            medicalCoverage,
            dependentsCount
        );
        
        employee.setCompanyEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@company.com");
        employee.setGender(gender);
        employee.setAddressLine1(addressLine1);
        employee.setAddressLine2(addressLine2);
        employee.setCity(city);
        employee.setState(state);
        employee.setZip(zip);
        
        return employee;
    }

    private int currentTestEmployeeId = 1;

    private String generateEmployeeId() {
        String leadingZeroes = currentTestEmployeeId < 10 ? "00" : currentTestEmployeeId < 100 ? "0" : "";
        String id = "EMP" + leadingZeroes + currentTestEmployeeId;
        currentTestEmployeeId++;

        return id;
    }
}