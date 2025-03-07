package grant.coburn.util;

import java.time.LocalDate;
import java.util.List;

import grant.coburn.dao.EmployeeDAO;
import grant.coburn.dao.PayrollRecordDAO;
import grant.coburn.dao.TimeEntryDAO;
import grant.coburn.model.Employee;
import grant.coburn.model.PayrollRecord;
import grant.coburn.model.TimeEntry;
import grant.coburn.util.PayrollCalculator.PayrollResult;

public class PayrollProcessor {
    private static PayrollProcessor instance;
    private final EmployeeDAO employeeDAO;
    private final TimeEntryDAO timeEntryDAO;
    private final PayrollRecordDAO payrollRecordDAO;

    private PayrollProcessor() {
        this.employeeDAO = EmployeeDAO.shared;
        this.timeEntryDAO = TimeEntryDAO.shared;
        this.payrollRecordDAO = PayrollRecordDAO.shared;
    }

    public static PayrollProcessor shared() {
        if (instance == null) {
            instance = new PayrollProcessor();
        }
        return instance;
    }

    /**
     * Process payroll for all employees for the given pay period
     * @param startDate The start date of the pay period
     * @param endDate The end date of the pay period
     * @return A PayrollProcessingResult containing the results of the operation
     */
    public PayrollProcessingResult processPayroll(LocalDate startDate, LocalDate endDate) {
        PayrollProcessingResult.Builder resultBuilder = new PayrollProcessingResult.Builder();

        if (startDate == null || endDate == null) {
            return resultBuilder.addError("Start date and end date are required").build();
        }

        if (endDate.isBefore(startDate)) {
            return resultBuilder.addError("End date cannot be before start date").build();
        }

        List<Employee> employees = employeeDAO.getAllEmployees();
        if (employees.isEmpty()) {
            return resultBuilder.addWarning("No employees found to process").build();
        }

        final int totalEmployees = employees.size();
        int successfullyProcessed = 0;

        resultBuilder.setEmployeesProcessed(totalEmployees);

        for (Employee employee : employees) {
            try {
                List<TimeEntry> timeEntries = timeEntryDAO.getTimeEntriesByEmployeeIdAndDateRange(
                    employee.getEmployeeId(), 
                    startDate, 
                    endDate
                );

                PayrollResult payrollResult = PayrollCalculator.calculatePayroll(
                    employee,
                    timeEntries,
                    startDate,
                    endDate
                );

                // Save the payroll record
                PayrollRecord record = new PayrollRecord(
                    employee.getEmployeeId(),
                    startDate,
                    endDate,
                    payrollResult.grossPay,
                    payrollResult.netPay,
                    payrollResult.medicalDeduction,
                    payrollResult.dependentStipend,
                    payrollResult.stateTax,
                    payrollResult.federalTax,
                    payrollResult.socialSecurityTax,
                    payrollResult.medicareTax,
                    payrollResult.employerSocialSecurityTax,
                    payrollResult.employerMedicareTax
                );

                payrollRecordDAO.savePayrollRecord(record);

                // Lock all time entries for the employee for the pay period
                lockTimeEntries(timeEntries);

                successfullyProcessed++;
            } catch (IllegalStateException e) {
                // Validation errors from PayrollCalculator
                resultBuilder.addError(String.format(
                    "Error processing employee %s (%s): %s",
                    employee.getFullName(),
                    employee.getEmployeeId(),
                    e.getMessage()
                ));
                resultBuilder.incrementEmployeesWithErrors();
            } catch (Exception e) {
                // Unexpected errors
                resultBuilder.addError(String.format(
                    "Unexpected error processing employee %s (%s): %s",
                    employee.getFullName(),
                    employee.getEmployeeId(),
                    e.getMessage()
                ));
                resultBuilder.incrementEmployeesWithErrors();
            }
        }

        if (successfullyProcessed < totalEmployees) {
            resultBuilder.addWarning(String.format(
                "Completed with errors: %d of %d employees processed successfully",
                successfullyProcessed,
                totalEmployees
            ));
        }

        return resultBuilder.build();
    }

    private  void lockTimeEntries(List<TimeEntry> timeEntries) {
        for (TimeEntry timeEntry : timeEntries) {
            timeEntry.setLocked(true);
            timeEntryDAO.updateTimeEntry(timeEntry);
        }
    }

    /**
     * Get payroll records for a specific date range.
     * @param startDate The start date of the pay period
     * @param endDate The end date of the pay period
     * @return List of payroll records
     */
    public List<PayrollRecord> getPayrollRecords(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return List.of();
        }
        return payrollRecordDAO.getPayrollRecordsByDateRange(startDate, endDate);
    }

    /**
     * Get all active employees.
     * @return List of all employees
     */
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    /**
     * Get an employee by their ID.
     * @param employeeId The ID of the employee to retrieve
     * @return The employee if found, null otherwise
     */
    public Employee getEmployeeById(String employeeId) {
        return employeeDAO.getEmployee(employeeId);
    }
} 