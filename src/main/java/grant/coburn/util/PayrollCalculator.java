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
    }

    /**
     * Calculate payroll for a specific pay period
     */
    public static PayrollResult calculatePayroll(
        Employee employee, 
        List<TimeEntry> timeEntries,
        LocalDate payPeriodStart,
        LocalDate payPeriodEnd
    ) {
        PayrollResult result = new PayrollResult();

        // Calculate base pay
        double payRate = employee.getBaseSalary();

        if (employee.getPayType() == Employee.PayType.SALARY) {
            // Calculate days in pay period (inclusive)
            long daysInPeriod = payPeriodStart.until(payPeriodEnd.plusDays(1)).getDays();
            double dailyRate = payRate / 365.0;  // Daily rate based on annual salary
            result.regularPay = dailyRate * daysInPeriod;
            result.overtimePay = 0;
        } else {
            for (TimeEntry entry : timeEntries) {
                result.regularPay += entry.getRegularHours() * payRate;
                result.overtimePay += entry.getOvertimeHours() * (payRate * 1.5);
            }
        }

        result.grossPay = result.regularPay + result.overtimePay;

        // Calculate deductions
        result.stateTax = result.grossPay * STATE_TAX_RATE;
        result.federalTax = result.grossPay * FEDERAL_TAX_RATE;
        result.socialSecurityTax = result.grossPay * SOCIAL_SECURITY_RATE;
        result.medicareTax = result.grossPay * MEDICARE_RATE;
        
        // Employer portions
        result.employerSocialSecurityTax = result.grossPay * SOCIAL_SECURITY_RATE;
        result.employerMedicareTax = result.grossPay * MEDICARE_RATE;

        // Medical and dependents
        result.medicalDeduction = (employee.getPayType() == Employee.PayType.SALARY) ? 
            ((employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE) ? SINGLE_MEDICAL_RATE : FAMILY_MEDICAL_RATE) : 0;
        result.dependentStipend = employee.getDependentsCount() * DEPENDENT_STIPEND;

        // Calculate net pay
        result.netPay = result.grossPay 
            - result.stateTax 
            - result.federalTax 
            - result.socialSecurityTax 
            - result.medicareTax 
            - result.medicalDeduction 
            + result.dependentStipend;

        return result;
    }

    /**
     * Calculate a preview of payroll for time entry views
     * This method is used to show estimated pay when entering time
     */
    public static PayrollResult calculatePayrollPreview(
        Employee employee, 
        List<TimeEntry> timeEntries
    ) {
        PayrollResult result = new PayrollResult();
        double payRate = employee.getBaseSalary();

        if (employee.getPayType() == Employee.PayType.SALARY) {
            // For preview, show daily rate
            result.regularPay = payRate / 365.0;
            result.overtimePay = 0;
        } else {
            for (TimeEntry entry : timeEntries) {
                result.regularPay += entry.getRegularHours() * payRate;
                result.overtimePay += entry.getOvertimeHours() * (payRate * 1.5);
            }
        }

        result.grossPay = result.regularPay + result.overtimePay;

        // Calculate deductions
        result.stateTax = result.grossPay * STATE_TAX_RATE;
        result.federalTax = result.grossPay * FEDERAL_TAX_RATE;
        result.socialSecurityTax = result.grossPay * SOCIAL_SECURITY_RATE;
        result.medicareTax = result.grossPay * MEDICARE_RATE;
        
        // Employer portions
        result.employerSocialSecurityTax = result.grossPay * SOCIAL_SECURITY_RATE;
        result.employerMedicareTax = result.grossPay * MEDICARE_RATE;

        // Medical and dependents
        result.medicalDeduction = (employee.getPayType() == Employee.PayType.SALARY) ? 
            ((employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE) ? SINGLE_MEDICAL_RATE : FAMILY_MEDICAL_RATE) : 0;
        result.dependentStipend = employee.getDependentsCount() * DEPENDENT_STIPEND;

        // Calculate net pay
        result.netPay = result.grossPay 
            - result.stateTax 
            - result.federalTax 
            - result.socialSecurityTax 
            - result.medicareTax 
            - result.medicalDeduction 
            + result.dependentStipend;

        return result;
    }
} 