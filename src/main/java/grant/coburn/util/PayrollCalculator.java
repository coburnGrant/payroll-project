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
            result.regularPay = Math.round(dailyRate * daysInPeriod * 100.0) / 100.0;  // Round final amount to 2 decimal places
            result.overtimePay = 0;
        } else {
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
            
            // Calculate regular pay for first 40 hours
            result.regularPay = Math.min(weeklyRegularHours, 40.0) * payRate;
            
            // Calculate overtime for hours over 40
            if (weeklyRegularHours > 40.0) {
                result.overtimePay = (weeklyRegularHours - 40.0) * (payRate * 1.5);
            }
            
            // Add PTO hours at regular rate
            result.regularPay += ptoHours * payRate;
        }

        result.grossPay = result.regularPay + result.overtimePay;

        // Calculate deductions
        result.stateTax = Math.round(result.grossPay * STATE_TAX_RATE * 100.0) / 100.0;
        result.federalTax = Math.round(result.grossPay * FEDERAL_TAX_RATE * 100.0) / 100.0;
        result.socialSecurityTax = Math.round(result.grossPay * SOCIAL_SECURITY_RATE * 100.0) / 100.0;
        result.medicareTax = Math.round(result.grossPay * MEDICARE_RATE * 100.0) / 100.0;
        
        // Employer portions
        result.employerSocialSecurityTax = Math.round(result.grossPay * SOCIAL_SECURITY_RATE * 100.0) / 100.0;
        result.employerMedicareTax = Math.round(result.grossPay * MEDICARE_RATE * 100.0) / 100.0;

        // Medical and dependents
        result.medicalDeduction = (employee.getPayType() == Employee.PayType.SALARY) ? 
            ((employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE) ? SINGLE_MEDICAL_RATE : FAMILY_MEDICAL_RATE) : 0;
        result.dependentStipend = employee.getDependentsCount() * DEPENDENT_STIPEND;

        // Calculate net pay
        result.netPay = Math.round((result.grossPay 
            - result.stateTax 
            - result.federalTax 
            - result.socialSecurityTax 
            - result.medicareTax 
            - result.medicalDeduction 
            + result.dependentStipend) * 100.0) / 100.0;

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
            
            // Calculate regular pay for first 40 hours
            result.regularPay = Math.min(weeklyRegularHours, 40.0) * payRate;
            
            // Calculate overtime for hours over 40
            if (weeklyRegularHours > 40.0) {
                result.overtimePay = (weeklyRegularHours - 40.0) * (payRate * 1.5);
            }
            
            // Add PTO hours at regular rate
            result.regularPay += ptoHours * payRate;
        }

        result.grossPay = result.regularPay + result.overtimePay;

        // Calculate deductions
        result.stateTax = Math.round(result.grossPay * STATE_TAX_RATE * 100.0) / 100.0;
        result.federalTax = Math.round(result.grossPay * FEDERAL_TAX_RATE * 100.0) / 100.0;
        result.socialSecurityTax = Math.round(result.grossPay * SOCIAL_SECURITY_RATE * 100.0) / 100.0;
        result.medicareTax = Math.round(result.grossPay * MEDICARE_RATE * 100.0) / 100.0;
        
        // Employer portions
        result.employerSocialSecurityTax = Math.round(result.grossPay * SOCIAL_SECURITY_RATE * 100.0) / 100.0;
        result.employerMedicareTax = Math.round(result.grossPay * MEDICARE_RATE * 100.0) / 100.0;

        // Medical and dependents
        result.medicalDeduction = (employee.getPayType() == Employee.PayType.SALARY) ? 
            ((employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE) ? SINGLE_MEDICAL_RATE : FAMILY_MEDICAL_RATE) : 0;
        result.dependentStipend = employee.getDependentsCount() * DEPENDENT_STIPEND;

        // Calculate net pay
        result.netPay = Math.round((result.grossPay 
            - result.stateTax 
            - result.federalTax 
            - result.socialSecurityTax 
            - result.medicareTax 
            - result.medicalDeduction 
            + result.dependentStipend) * 100.0) / 100.0;

        return result;
    }
} 