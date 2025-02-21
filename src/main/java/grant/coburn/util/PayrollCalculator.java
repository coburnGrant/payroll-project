package grant.coburn.util;

import java.util.List;

import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;

public class PayrollCalculator {
    private static final double STATE_TAX_RATE = 0.0315;  // IN 3.15%
    private static final double FEDERAL_TAX_RATE = 0.0765;  // 7.65%
    private static final double SOCIAL_SECURITY_RATE = 0.062;  // 6.2%
    private static final double MEDICARE_RATE = 0.0145;  // 1.45%
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

    public static PayrollResult calculatePayroll(Employee employee, List<TimeEntry> timeEntries, double hourlyRate) {
        PayrollResult result = new PayrollResult();

        // Calculate base pay
        if (employee.getPayType() == Employee.PayType.SALARY) {
            result.regularPay = hourlyRate * 40;  // Assuming weekly salary
            result.overtimePay = 0;
        } else {
            for (TimeEntry entry : timeEntries) {
                result.regularPay += entry.getRegularHours() * hourlyRate;
                result.overtimePay += entry.getOvertimeHours() * (hourlyRate * 1.5);
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
        result.medicalDeduction = (employee.getMedicalCoverage() == Employee.MedicalCoverage.SINGLE) 
            ? SINGLE_MEDICAL_RATE 
            : FAMILY_MEDICAL_RATE;
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