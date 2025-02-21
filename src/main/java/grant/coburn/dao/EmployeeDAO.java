package grant.coburn.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import grant.coburn.model.Employee;
import grant.coburn.model.User;
import grant.coburn.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public ObservableList<Employee> getAllEmployees() {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employees";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                employees.add(createEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public Employee getEmployee(String employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

    public EmployeeCredentials createEmployee(Employee employee) {
        Connection conn = null;
        try {
            conn = dbUtil.getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO employees (employee_id, department, job_title, first_name, last_name, " +
                        "status, date_of_birth, hire_date, pay_type, base_salary, medical_coverage, dependents_count, " +
                        "company_email, gender, address_line1, address_line2, city, state, zip, picture_path) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                setEmployeeParameters(pstmt, employee);
                pstmt.executeUpdate();

                String tempPassword = generateSecurePassword();
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
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String generateSecurePassword() {
        // Generate a random 12-character password with letters, numbers, and special characters
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
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

            System.out.println("Executing SQL: " + pstmt.toString());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEmployee(String employeeId) {
        Connection conn = null;
        try {
            conn = dbUtil.getConnection();
            conn.setAutoCommit(false);

            // First delete the user
            String deleteUserSql = "DELETE FROM users WHERE employee_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
                pstmt.setString(1, employeeId);
                pstmt.executeUpdate();
            }

            // Then delete the employee
            String deleteEmployeeSql = "DELETE FROM employees WHERE employee_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteEmployeeSql)) {
                pstmt.setString(1, employeeId);
                int rowsAffected = pstmt.executeUpdate();
                
                conn.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
}