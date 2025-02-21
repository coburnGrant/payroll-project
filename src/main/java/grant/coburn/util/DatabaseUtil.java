package grant.coburn.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private Connection connection;

    public static final DatabaseUtil shared = new DatabaseUtil();

    private DatabaseUtil() {
        try {
            connection = getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Gets the current database connection or creates a new one if needed */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = getSqlUrl(DBProperties.HOST, DBProperties.PORT, DBProperties.DB_NAME);
            connection = DriverManager.getConnection(url, DBProperties.USER, DBProperties.PASSWORD);
        }
        return connection;
    }

    /** Helper function to format a SQL URL string */
    public static String getSqlUrl(String host, int port, String dbName) {
        return String.format("jdbc:mysql://%s:%d/%s", host, port, dbName);
    }

    /** Closes the database connection */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 