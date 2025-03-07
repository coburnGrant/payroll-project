package grant.coburn.report;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.opencsv.CSVWriter;

import grant.coburn.dao.PayrollRecordDAO;
import grant.coburn.model.Employee;
import grant.coburn.model.PayrollRecord;
import grant.coburn.util.PayrollProcessor;

/**
 * Implementation of ReportGenerator for CSV format.
 */
public class CSVReportGenerator implements ReportGenerator {
    private final PayrollRecordDAO payrollRecordDAO;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final String[] HEADERS = {
        "Employee ID",
        "Employee Name",
        "Pay Period Start",
        "Pay Period End",
        "Gross Pay",
        "Net Pay",
        "Medical Deduction",
        "Dependent Stipend",
        "State Tax",
        "Federal Tax",
        "Social Security Tax",
        "Medicare Tax",
        "Employer Social Security",
        "Employer Medicare",
        "Overtime Pay",
        "Total Deductions"
    };

    public CSVReportGenerator() {
        this.payrollRecordDAO = PayrollRecordDAO.shared;
    }

    @Override
    public void generateReport(String outputPath, LocalDate startDate, LocalDate endDate) throws IOException {
        List<PayrollRecord> records = payrollRecordDAO.getPayrollRecordsByDateRange(startDate, endDate);

        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath))) {
            // Write headers
            writer.writeNext(HEADERS);

            // Write data rows
            for (PayrollRecord record : records) {
                Employee employee = PayrollProcessor.shared().getEmployeeById(record.getEmployeeId());
                String employeeName = employee != null ? employee.getFullName() : "Unknown";

                String[] row = {
                    record.getEmployeeId(),
                    employeeName,
                    record.getPayPeriodStart().format(DATE_FORMATTER),
                    record.getPayPeriodEnd().format(DATE_FORMATTER),
                    formatMoney(record.getGrossPay()),
                    formatMoney(record.getNetPay()),
                    formatMoney(record.getMedicalDeduction()),
                    formatMoney(record.getDependentStipend()),
                    formatMoney(record.getStateTax()),
                    formatMoney(record.getFederalTax()),
                    formatMoney(record.getSocialSecurityTax()),
                    formatMoney(record.getMedicareTax()),
                    formatMoney(record.getEmployerSocialSecurity()),
                    formatMoney(record.getEmployerMedicare()),
                    formatMoney(record.getOvertimePay()),
                    formatMoney(record.getTotalDeductions())
                };
                writer.writeNext(row);
            }
        }
    }

    private String formatMoney(double amount) {
        return String.format("%.2f", amount);
    }
} 