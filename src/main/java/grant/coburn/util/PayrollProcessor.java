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
     * Process payroll for all active employees within the specified date range.
     * @param startDate The start date of the pay period
     * @param endDate The end date of the pay period
     * @return true if processing was successful, false otherwise
     */
    public boolean processPayroll(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return false;
        }

        // Get all active employees
        List<Employee> employees = employeeDAO.getAllEmployees();
        if (employees.isEmpty()) {
            return false;
        }

        // Process payroll for each employee
        for (Employee employee : employees) {
            if (!processEmployeePayroll(employee, startDate, endDate)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Process payroll for a single employee within the specified date range.
     * @param employee The employee to process payroll for
     * @param startDate The start date of the pay period
     * @param endDate The end date of the pay period
     * @return true if processing was successful, false otherwise
     */
    private boolean processEmployeePayroll(Employee employee, LocalDate startDate, LocalDate endDate) {
        // Get time entries for the pay period that are not locked
        List<TimeEntry> timeEntries = timeEntryDAO.getTimeEntriesByEmployeeIdAndDateRange(
            employee.getEmployeeId(), 
            startDate, 
            endDate
        );

        if (employee.getPayType() == Employee.PayType.SALARY) {
            return processSalariedEmployeePayroll(employee, timeEntries, startDate, endDate);
        } else {
            return processHourlyEmployeePayroll(employee, timeEntries, startDate, endDate);
        }
    }

    /**
     * Process payroll for a salaried employee.
     * @param employee The salaried employee
     * @param timeEntries List of time entries (only used for PTO)
     * @param startDate The start date of the pay period
     * @param endDate The end date of the pay period
     * @return true if processing was successful, false otherwise
     */
    private boolean processSalariedEmployeePayroll(
        Employee employee, 
        List<TimeEntry> timeEntries, 
        LocalDate startDate,
        LocalDate endDate
    ) {
        // Calculate payroll with salary (time entries only used for PTO deductions)
        PayrollResult result = PayrollCalculator.calculatePayroll(
            employee,
            timeEntries,
            startDate,
            endDate
        );

        // Create and save payroll record
        PayrollRecord record = createPayrollRecord(employee, startDate, endDate, result);

        if (!payrollRecordDAO.savePayrollRecord(record)) {
            return false;
        }

        // Lock only PTO entries
        return lockPTOEntries(timeEntries);
    }

    /**
     * Process payroll for an hourly employee.
     * @param employee The hourly employee
     * @param timeEntries List of time entries
     * @param startDate The start date of the pay period
     * @param endDate The end date of the pay period
     * @return true if processing was successful, false otherwise
     */
    private boolean processHourlyEmployeePayroll(
        Employee employee,
        List<TimeEntry> timeEntries,
        LocalDate startDate,
        LocalDate endDate
    ) {
        // Calculate payroll with time entries (will be zero if no entries)
        PayrollResult result = PayrollCalculator.calculatePayroll(
            employee,
            timeEntries,
            startDate,
            endDate
        );

        // Create and save payroll record
        PayrollRecord record = createPayrollRecord(employee, startDate, endDate, result);
        if (!payrollRecordDAO.savePayrollRecord(record)) {
            return false;
        }

        // Lock all time entries (if any exist)
        if (!timeEntries.isEmpty()) {
            return lockAllTimeEntries(timeEntries);
        }

        return true;
    }

    /**
     * Create a payroll record from the calculation result.
     */
    private PayrollRecord createPayrollRecord(
        Employee employee,
        LocalDate startDate,
        LocalDate endDate,
        PayrollResult result
    ) {
        return new PayrollRecord(
            employee.getEmployeeId(),
            startDate,
            endDate,
            result.grossPay,
            result.netPay,
            result.medicalDeduction,
            result.dependentStipend,
            result.stateTax,
            result.federalTax,
            result.socialSecurityTax,
            result.medicareTax,
            result.employerSocialSecurityTax,
            result.employerMedicareTax
        );
    }

    /**
     * Lock all PTO time entries.
     */
    private boolean lockPTOEntries(List<TimeEntry> timeEntries) {
        for (TimeEntry entry : timeEntries) {
            if (entry.isPto()) {
                entry.setLocked(true);
                if (!timeEntryDAO.updateTimeEntry(entry)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Lock all time entries.
     */
    private boolean lockAllTimeEntries(List<TimeEntry> timeEntries) {
        for (TimeEntry entry : timeEntries) {
            entry.setLocked(true);
            if (!timeEntryDAO.updateTimeEntry(entry)) {
                return false;
            }
        }
        return true;
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