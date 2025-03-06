package grant.coburn.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import grant.coburn.model.PayrollRecord;
import grant.coburn.util.DatabaseUtil;

public class PayrollRecordDAO {
    public static final PayrollRecordDAO shared = new PayrollRecordDAO(DatabaseUtil.shared);
    private final DatabaseUtil dbUtil;

    private PayrollRecordDAO(DatabaseUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public boolean savePayrollRecord(PayrollRecord record) {
        String sql = "INSERT INTO payroll_records (employee_id, pay_period_start, pay_period_end, " +
                    "gross_pay, net_pay, medical_deduction, dependent_stipend, state_tax, " +
                    "federal_tax, social_security_tax, medicare_tax, employer_social_security, " +
                    "employer_medicare) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, record.getEmployeeId());
            stmt.setDate(2, java.sql.Date.valueOf(record.getPayPeriodStart()));
            stmt.setDate(3, java.sql.Date.valueOf(record.getPayPeriodEnd()));
            stmt.setDouble(4, record.getGrossPay());
            stmt.setDouble(5, record.getNetPay());
            stmt.setDouble(6, record.getMedicalDeduction());
            stmt.setDouble(7, record.getDependentStipend());
            stmt.setDouble(8, record.getStateTax());
            stmt.setDouble(9, record.getFederalTax());
            stmt.setDouble(10, record.getSocialSecurityTax());
            stmt.setDouble(11, record.getMedicareTax());
            stmt.setDouble(12, record.getEmployerSocialSecurity());
            stmt.setDouble(13, record.getEmployerMedicare());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PayrollRecord> getPayrollRecordsByEmployee(String employeeId) {
        List<PayrollRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM payroll_records WHERE employee_id = ? ORDER BY pay_period_start DESC";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(createPayrollRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public List<PayrollRecord> getPayrollRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<PayrollRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM payroll_records WHERE pay_period_start >= ? AND pay_period_end <= ? " +
                    "ORDER BY pay_period_start DESC";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(createPayrollRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public PayrollRecord getLatestPayrollRecord(String employeeId) {
        String sql = "SELECT * FROM payroll_records WHERE employee_id = ? ORDER BY pay_period_start DESC LIMIT 1";

        try (Connection conn = dbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createPayrollRecordFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private PayrollRecord createPayrollRecordFromResultSet(ResultSet rs) throws SQLException {
        PayrollRecord record = new PayrollRecord(
            rs.getString("employee_id"),
            rs.getDate("pay_period_start").toLocalDate(),
            rs.getDate("pay_period_end").toLocalDate(),
            rs.getDouble("gross_pay"),
            rs.getDouble("net_pay"),
            rs.getDouble("medical_deduction"),
            rs.getDouble("dependent_stipend"),
            rs.getDouble("state_tax"),
            rs.getDouble("federal_tax"),
            rs.getDouble("social_security_tax"),
            rs.getDouble("medicare_tax"),
            rs.getDouble("employer_social_security"),
            rs.getDouble("employer_medicare")
        );
        record.setRecordId(rs.getLong("record_id"));
        record.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        return record;
    }
} 