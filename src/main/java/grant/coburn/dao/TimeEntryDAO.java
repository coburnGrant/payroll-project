package grant.coburn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import grant.coburn.model.TimeEntry;
import grant.coburn.util.DatabaseUtil;

public class TimeEntryDAO {
    private static final TimeEntryDAO shared = new TimeEntryDAO();
    private final DatabaseUtil dbUtil = DatabaseUtil.shared;

    private TimeEntryDAO() {}

    public static TimeEntryDAO shared() {
        return shared;
    }

    public List<TimeEntry> getTimeEntriesByEmployeeId(String employeeId) {
        List<TimeEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM time_entries WHERE employee_id = ? ORDER BY work_date DESC";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TimeEntry entry = new TimeEntry(
                        rs.getString("employee_id"),
                        rs.getDate("work_date").toLocalDate(),
                        rs.getDouble("hours_worked"),
                        rs.getBoolean("is_pto")
                    );
                    entry.setEntryId(rs.getLong("entry_id"));
                    entry.setLocked(rs.getBoolean("is_locked"));
                    entries.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching time entries: " + e.getMessage());
        }

        return entries;
    }

    public boolean saveTimeEntry(TimeEntry entry) {
        String sql = "INSERT INTO time_entries (employee_id, work_date, hours_worked, is_pto, is_locked) " +
                    "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, entry.getEmployeeId());
            stmt.setDate(2, java.sql.Date.valueOf(entry.getWorkDate()));
            stmt.setDouble(3, entry.getHoursWorked());
            stmt.setBoolean(4, entry.isPto());
            stmt.setBoolean(5, entry.isLocked());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving time entry: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTimeEntry(TimeEntry entry) {
        String sql = "UPDATE time_entries SET work_date = ?, hours_worked = ?, is_pto = ? " +
                    "WHERE entry_id = ? AND employee_id = ? AND is_locked = FALSE";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(entry.getWorkDate()));
            stmt.setDouble(2, entry.getHoursWorked());
            stmt.setBoolean(3, entry.isPto());
            stmt.setLong(4, entry.getEntryId());
            stmt.setString(5, entry.getEmployeeId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating time entry: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTimeEntry(TimeEntry entry) {
        String sql = "DELETE FROM time_entries WHERE entry_id = ? AND employee_id = ? AND is_locked = FALSE";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, entry.getEntryId());
            stmt.setString(2, entry.getEmployeeId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting time entry: " + e.getMessage());
            return false;
        }
    }
} 