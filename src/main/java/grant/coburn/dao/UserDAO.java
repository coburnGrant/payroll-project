package grant.coburn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import grant.coburn.model.User;
import grant.coburn.util.DatabaseUtil;
import grant.coburn.util.PasswordUtil;

public class UserDAO {
    private final DatabaseUtil dbUtil;

    public static final UserDAO shared = new UserDAO(DatabaseUtil.shared);

    public UserDAO(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public User authenticateUser(String userId, String password) {
        String sql = "SELECT u.*, e.employee_id FROM users u " +
                     "LEFT JOIN employees e ON u.user_id = e.user_id " +
                     "WHERE u.user_id = ?";
        
        try {
            Connection conn = dbUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                
                // Verify the password using BCrypt
                if (PasswordUtil.bcryptCheckPassword(password, hashedPassword)) {

                    User user = new User(
                        rs.getString("user_id"),
                        hashedPassword,
                        User.UserType.valueOf(rs.getString("user_type")),
                        rs.getString("email")
                    );
                    
                    // Get employee_id from the join
                    String employeeId = rs.getString("employee_id");
                    if (!rs.wasNull()) {
                        user.setEmployeeId(employeeId);
                    }
                    
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(User user, Connection conn) throws SQLException {
        String sql = "INSERT INTO users (user_id, password, user_type, email, employee_id, must_change_password) " +
                    "VALUES (?, ?, ?, ?, ?, TRUE)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String hashedPassword = PasswordUtil.bcryptPassword(user.getPassword());
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getUserType().toString());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getEmployeeId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean createUser(User user) {
        try (Connection conn = dbUtil.getConnection()) {
            return createUser(user, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAllUsers() {
        String sql = "DELETE FROM users";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean mustChangePassword(String userId) {
        String sql = "SELECT must_change_password FROM users WHERE user_id = ?";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("must_change_password");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        // First verify current password
        String sql = "SELECT password FROM users WHERE user_id = ?";
        
        try (Connection conn = dbUtil.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        if (!PasswordUtil.bcryptCheckPassword(currentPassword, hashedPassword)) {
                            return false;
                        }
                    }
                }
            }

            // Update password and reset must_change_password flag
            sql = "UPDATE users SET password = ?, must_change_password = FALSE WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String newHashedPassword = PasswordUtil.bcryptPassword(newPassword);
                pstmt.setString(1, newHashedPassword);
                pstmt.setString(2, userId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 