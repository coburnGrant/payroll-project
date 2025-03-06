package grant.coburn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import grant.coburn.model.TimeEntry;
import grant.coburn.util.DatabaseUtil;

public class TimeEntryDAO {
    public static final TimeEntryDAO shared = new TimeEntryDAO(DatabaseUtil.shared);
    private final DatabaseUtil dbUtil;

    private TimeEntryDAO(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
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

    /**
     * Get time entries for an employee within a specific date range that are not locked.
     * @param employeeId The ID of the employee
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return List of time entries
     */
    public List<TimeEntry> getTimeEntriesByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        List<TimeEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM time_entries WHERE employee_id = ? AND work_date BETWEEN ? AND ? AND is_locked = false";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            stmt.setDate(2, java.sql.Date.valueOf(startDate));
            stmt.setDate(3, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(extractTimeEntryFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return entries;
    }

    /**
     * Extract a TimeEntry object from a ResultSet.
     * @param rs The ResultSet containing the time entry data
     * @return A TimeEntry object
     * @throws SQLException if there's an error reading from the ResultSet
     */
    private TimeEntry extractTimeEntryFromResultSet(ResultSet rs) throws SQLException {
        TimeEntry entry = new TimeEntry(
            rs.getString("employee_id"),
            rs.getDate("work_date").toLocalDate(),
            rs.getDouble("hours_worked"),
            rs.getBoolean("is_pto")
        );
        entry.setEntryId(rs.getLong("entry_id"));
        entry.setLocked(rs.getBoolean("is_locked"));
        return entry;
    }
} 