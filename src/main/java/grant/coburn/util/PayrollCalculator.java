package grant.coburn.util;

import java.time.LocalDate;
import java.util.List;

import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;

public class PayrollCalculator {
    public static final double STATE_TAX_RATE = 0.0315;  // IN 3.15%
    public static final double FEDERAL_TAX_RATE = 0.0765;  // 7.65%
    public static final double SOCIAL_SECURITY_RATE = 0.062;  // 6.2%
    public static final double MEDICARE_RATE = 0.0145;  // 1.45%
    private static final double SINGLE_MEDICAL_RATE = 50.0;
    private static final double FAMILY_MEDICAL_RATE = 100.0;
    private static final double DEPENDENT_STIPEND = 45.0;
    private static final double HOURLY_OVERTIME_RATE = 1.5;
    private static final double WORK_WEEK_HOURS = 40.0;

    /**
     * Class representing the result of the payroll calculation
     */
    public static class PayrollResult {
        public double grossPay;
        public double regularPay;
        public double overtimePay;
        public double stateTax;
        public double federalTax;
        public double socialSecurityTax;
        public double medicareTax;
        public double employerSocialSecurityTax;
        public double employerMedicareTax;
        public double medicalDeduction;
        public double dependentStipend;
        public double netPay;
        public LocalDate payPeriodStart;
        public LocalDate payPeriodEnd;

        public void accept(PayrollResultVisitor visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Determine the regular pay for a salaried employee
     * @param salary The annual salary of the employee
     * @param payPeriodStart The start date of the pay period
     * @param payPeriodEnd The end date of the pay period
     * @return The regular pay for the employee
     */
    private static double determineSalaryRegularPay(double salary, LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        // Calculate days in pay period (inclusive)
        long daysInPeriod = payPeriodStart.until(payPeriodEnd.plusDays(1)).getDays();
        double dailyRate = salary / 365.0;  // Daily rate based on annual salary
        double regularPay = dailyRate * daysInPeriod;
        return regularPay;
    }

    /**
     * Determine the regular pay for an hourly employee
     * @param hourlyRate The hourly rate of the employee
     * @param timeEntries The time entries to calculate regular pay for
     * @param result The payroll result to calculate regular pay for
     */
    private static void determineHourlyRegularPay(double hourlyRate, List<TimeEntry> timeEntries, PayrollResult result) {
        // For hourly employees, calculate weekly overtime
        double weeklyRegularHours = 0.0;
        double ptoHours = 0.0;
        
        for (TimeEntry entry : timeEntries) {
            if (entry.isPto()) {
                ptoHours += entry.getRegularHours();
            } else {
                weeklyRegularHours += entry.getRegularHours();
            }
        }
        
        // Calculate regular pay for first 40 hours plus PTO
        double regularHoursPay = Math.min(weeklyRegularHours, WORK_WEEK_HOURS) * hourlyRate;
        double ptoPay = ptoHours * hourlyRate;
        result.regularPay = regularHoursPay + ptoPay;
        
        // Calculate overtime for hours over 40
        if (weeklyRegularHours > WORK_WEEK_HOURS) {
            result.overtimePay = (weeklyRegularHours - WORK_WEEK_HOURS) * (hourlyRate * HOURLY_OVERTIME_RATE);
        }
    }

    /**
     * Calculate deductions and net pay for an employee. Assumes that the gross pay has already been calculated.
     * @param result The payroll result to calculate deductions and net pay for
     * @param employee The employee to calculate deductions and net pay for
     */
    private static void calculateDeductionsAndNetPay(PayrollResult result, Employee employee) {
        // Calculate deductions
        result.stateTax = result.grossPay * STATE_TAX_RATE;
        result.federalTax = result.grossPay * FEDERAL_TAX_RATE;
        result.socialSecurityTax = result.grossPay * SOCIAL_SECURITY_RATE;
        result.medicareTax = result.grossPay * MEDICARE_RATE;
        
        // Employer portions
        result.employerSocialSecurityTax = result.grossPay * SOCIAL_SECURITY_RATE;
        result.employerMedicareTax = result.grossPay * MEDICARE_RATE;

        // Medical and dependents
        result.medicalDeduction = determineMedicalDeduction(employee);
        result.dependentStipend = employee.getDependentsCount() * DEPENDENT_STIPEND;

        // Calculate net pay
        result.netPay = result.grossPay 
            - result.stateTax 
            - result.federalTax 
            - result.socialSecurityTax 
            - result.medicareTax 
            - result.medicalDeduction 
            + result.dependentStipend;
    }

    /**
     * Determine the medical deduction for an employee
     * @param employee The employee to determine the medical deduction for
     * @return The medical deduction for the employee
     */
    private static double determineMedicalDeduction(Employee employee) {
        // Salaried employees have a medical deduction
        if (employee.getPayType() == Employee.PayType.SALARY) {
            // Determine medical deduction based on medical coverage
            return (employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE) ? SINGLE_MEDICAL_RATE : FAMILY_MEDICAL_RATE;
        }

        // Hourly employees have no medical deduction
        return 0;
    }

    /**
     * Calculate payroll for an employee
     * @param employee The employee to calculate payroll for
     * @param timeEntries The time entries to calculate payroll for
     * @param payPeriodStart The start date of the pay period
     * @param payPeriodEnd The end date of the pay period
     * @return The calculated payroll result
     */
    public static PayrollResult calculatePayroll(
        Employee employee,
        List<TimeEntry> timeEntries, 
        LocalDate payPeriodStart,
        LocalDate payPeriodEnd
    ) {
        PayrollResult result = new PayrollResult();
        result.payPeriodStart = payPeriodStart;
        result.payPeriodEnd = payPeriodEnd;

        double payRate = employee.getBaseSalary();

        if (employee.getPayType() == Employee.PayType.SALARY) {
            result.regularPay = determineSalaryRegularPay(
                payRate,
                payPeriodStart, 
                payPeriodEnd
            );

            // Salaried employees don't have overtime
            result.overtimePay = 0;
        } else {
            determineHourlyRegularPay(
                payRate, 
                timeEntries, 
                result
            );
        }

        result.grossPay = result.regularPay + result.overtimePay;
        
        calculateDeductionsAndNetPay(result, employee);

        validatePayrollResult(result);

        return result;
    }
    
    /**
     * Validate the payroll result
     * @param result The payroll result to validate
     */
    private static void validatePayrollResult(PayrollResult result) {
        // Round all monetary values using visitor
        result.accept(new MonetaryRoundingVisitor());

        // Validate calculations using validation visitor
        PayrollResultValidationVisitor validator = new PayrollResultValidationVisitor();
        
        result.accept(validator);

        if (validator.hasErrors()) {
            System.out.println("Payroll validation failed:\n" + validator.getErrors());
        }
    }

    /**
     * Calculate a preview of payroll for time entry views
     * This method is used to show estimated pay when entering time
     * @param employee The employee to calculate payroll for
     * @param timeEntries The time entries to calculate payroll for
     * @return The calculated payroll result
     */
    public static PayrollResult calculatePayrollPreview(
        Employee employee, 
        List<TimeEntry> timeEntries
    ) {
        PayrollResult result = new PayrollResult();
        double payRate = employee.getBaseSalary();

        if (employee.getPayType() == Employee.PayType.SALARY) {
            // For preview, just show the salary
            result.regularPay = payRate;
        } else {
            determineHourlyRegularPay(payRate, timeEntries, result);
        }

        result.grossPay = result.regularPay + result.overtimePay;

        calculateDeductionsAndNetPay(result, employee);

        validatePayrollResult(result);

        return result;
    }
}