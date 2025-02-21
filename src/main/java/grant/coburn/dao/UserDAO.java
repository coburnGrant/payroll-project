package grant.coburn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import grant.coburn.model.User;
import grant.coburn.util.DatabaseUtil;

public class UserDAO {
    private final DatabaseUtil dbUtil;

    public static final UserDAO shared = new UserDAO(DatabaseUtil.shared);

    public UserDAO(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public User authenticateUser(String userId, String password) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    
                    // Verify the password using BCrypt
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return new User(
                            rs.getString("user_id"),
                            hashedPassword,
                            User.UserType.valueOf(rs.getString("user_type")),
                            rs.getString("email")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (user_id, password, user_type, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getUserType().toString());
            pstmt.setString(4, user.getEmail());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
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
} 