package grant.coburn.util;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import grant.coburn.util.PayrollCalculator.PayrollResult;

class PayrollResultValidationVisitorTest {
    private PayrollResultValidationVisitor visitor;
    private PayrollResult result;

    @BeforeEach
    void setUp() {
        visitor = new PayrollResultValidationVisitor();
        result = new PayrollCalculator.PayrollResult();
        
        // Set up a valid payroll result
        result.regularPay = 1000.00;
        result.overtimePay = 150.00;
        result.grossPay = 1150.00;
        result.stateTax = result.grossPay * PayrollCalculator.STATE_TAX_RATE;
        result.federalTax = result.grossPay * PayrollCalculator.FEDERAL_TAX_RATE;
        result.socialSecurityTax = result.grossPay * PayrollCalculator.SOCIAL_SECURITY_RATE;
        result.medicareTax = result.grossPay * PayrollCalculator.MEDICARE_RATE;
        result.employerSocialSecurityTax = result.socialSecurityTax;
        result.employerMedicareTax = result.medicareTax;
        result.medicalDeduction = 50.00;
        result.dependentStipend = 90.00;  // 2 dependents
        
        // Calculate net pay
        result.netPay = result.grossPay 
            - result.stateTax 
            - result.federalTax 
            - result.socialSecurityTax 
            - result.medicareTax 
            - result.medicalDeduction 
            + result.dependentStipend;

        // Set valid pay period dates
        result.payPeriodStart = LocalDate.of(2024, 1, 1);
        result.payPeriodEnd = LocalDate.of(2024, 1, 14);
    }

    @Test
    void testValidPayrollResult() {
        visitor.visit(result);
        assertFalse(visitor.hasErrors(), "Valid payroll result should have no errors");
        assertTrue(visitor.getErrors().isEmpty(), "Error message should be empty for valid result");
    }

    @Test
    void testInvalidGrossPay() {
        result.grossPay = 1000.00;  // Should be 1150.00
        visitor.visit(result);
        assertTrue(visitor.hasErrors());
        assertTrue(visitor.getErrors().contains("Gross pay does not match"));
    }

    @Test
    void testInvalidNetPay() {
        result.netPay += 100.00;  // Add error to net pay
        visitor.visit(result);
        assertTrue(visitor.hasErrors());
        assertTrue(visitor.getErrors().contains("Net pay calculation is incorrect"));
    }

    @Test
    void testInvalidTaxRates() {
        result.stateTax = result.grossPay * 0.05;  // Wrong rate
        visitor.visit(result);
        assertTrue(visitor.hasErrors());
        assertTrue(visitor.getErrors().contains("State tax rate is incorrect"));
    }

    @Test
    void testMismatchedEmployerTaxes() {
        result.employerSocialSecurityTax = result.socialSecurityTax + 1.00;
        visitor.visit(result);
        assertTrue(visitor.hasErrors());
        assertTrue(visitor.getErrors().contains("Employer Social Security tax does not match"));
    }

    @Test
    void testInvalidPayPeriodDates() {
        result.payPeriodEnd = result.payPeriodStart.minusDays(1);
        visitor.visit(result);
        assertTrue(visitor.hasErrors());
        assertTrue(visitor.getErrors().contains("Pay period end date is before start date"));
    }

    @Test
    void testNegativeValues() {
        result.regularPay = -100.00;
        visitor.visit(result);
        assertTrue(visitor.hasErrors());
        assertTrue(visitor.getErrors().contains("Regular pay cannot be negative"));
    }

    @Test
    void testMultipleErrors() {
        // Introduce multiple errors
        result.grossPay = 1000.00;  // Invalid gross pay
        result.netPay += 100.00;    // Invalid net pay
        result.stateTax = 0.00;     // Invalid tax rate
        result.regularPay = -50.00;  // Negative value

        visitor.visit(result);
        assertTrue(visitor.hasErrors());
        String errors = visitor.getErrors();
        
        assertTrue(errors.contains("Gross pay does not match"));
        assertTrue(errors.contains("Net pay calculation is incorrect"));
        assertTrue(errors.contains("State tax rate is incorrect"));
        assertTrue(errors.contains("Regular pay cannot be negative"));
    }

    @Test
    void testZeroGrossPay() {
        // Set up a valid zero pay result
        result.regularPay = 0.00;
        result.overtimePay = 0.00;
        result.grossPay = 0.00;
        result.stateTax = 0.00;
        result.federalTax = 0.00;
        result.socialSecurityTax = 0.00;
        result.medicareTax = 0.00;
        result.employerSocialSecurityTax = 0.00;
        result.employerMedicareTax = 0.00;
        result.netPay = result.dependentStipend - result.medicalDeduction;

        visitor.visit(result);
        assertFalse(visitor.hasErrors(), "Zero gross pay should be valid");
    }
} 