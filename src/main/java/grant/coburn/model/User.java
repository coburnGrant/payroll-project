package grant.coburn.model;

public class User {
    private String userId;
    private String password;
    private UserType userType;
    private String email;
    private String employeeId;

    public enum UserType {
        ADMIN,
        EMPLOYEE
    }
    
    public User(String userId, String password, UserType userType, String email) {
        this.userId = userId;
        this.password = password;
        this.userType = userType;
        this.email = email;
    }

    // MARK: - Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
} 