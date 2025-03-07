package grant.coburn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import grant.coburn.util.PayrollCalculator.PayrollResult;

class MonetaryRoundingVisitorTest {
    private final MonetaryRoundingVisitor visitor = new MonetaryRoundingVisitor();
    private static final double DELTA = 0.001;

    @Test
    void testRoundingUp() {
        PayrollResult result = new PayrollCalculator.PayrollResult();
        result.regularPay = 100.555;
        result.overtimePay = 50.556;
        result.grossPay = 151.111;
        result.stateTax = 4.766;
        result.federalTax = 11.559;
        result.socialSecurityTax = 9.369;
        result.medicareTax = 2.191;
        result.employerSocialSecurityTax = 9.369;
        result.employerMedicareTax = 2.191;
        result.medicalDeduction = 50.005;
        result.dependentStipend = 45.009;
        result.netPay = 120.666;

        visitor.visit(result);

        assertEquals(100.56, result.regularPay, DELTA, "Regular pay should round to 100.56");
        assertEquals(50.56, result.overtimePay, DELTA, "Overtime pay should round to 50.56");
        assertEquals(151.11, result.grossPay, DELTA, "Gross pay should round to 151.11");
        assertEquals(4.77, result.stateTax, DELTA, "State tax should round to 4.77");
        assertEquals(11.56, result.federalTax, DELTA, "Federal tax should round to 11.56");
        assertEquals(9.37, result.socialSecurityTax, DELTA, "Social security tax should round to 9.37");
        assertEquals(2.19, result.medicareTax, DELTA, "Medicare tax should round to 2.19");
        assertEquals(9.37, result.employerSocialSecurityTax, DELTA, "Employer social security tax should round to 9.37");
        assertEquals(2.19, result.employerMedicareTax, DELTA, "Employer medicare tax should round to 2.19");
        assertEquals(50.01, result.medicalDeduction, DELTA, "Medical deduction should round to 50.01");
        assertEquals(45.01, result.dependentStipend, DELTA, "Dependent stipend should round to 45.01");
        assertEquals(120.67, result.netPay, DELTA, "Net pay should round to 120.67");
    }

    @Test
    void testRoundingDown() {
        PayrollResult result = new PayrollCalculator.PayrollResult();
        result.regularPay = 100.554;
        result.overtimePay = 50.554;
        result.grossPay = 151.114;
        result.stateTax = 4.764;
        result.federalTax = 11.554;
        result.socialSecurityTax = 9.364;
        result.medicareTax = 2.194;
        result.employerSocialSecurityTax = 9.364;
        result.employerMedicareTax = 2.194;
        result.medicalDeduction = 50.004;
        result.dependentStipend = 45.004;
        result.netPay = 120.664;

        visitor.visit(result);

        assertEquals(100.55, result.regularPay, DELTA, "Regular pay should round to 100.55");
        assertEquals(50.55, result.overtimePay, DELTA, "Overtime pay should round to 50.55");
        assertEquals(151.11, result.grossPay, DELTA, "Gross pay should round to 151.11");
        assertEquals(4.76, result.stateTax, DELTA, "State tax should round to 4.76");
        assertEquals(11.55, result.federalTax, DELTA, "Federal tax should round to 11.55");
        assertEquals(9.36, result.socialSecurityTax, DELTA, "Social security tax should round to 9.36");
        assertEquals(2.19, result.medicareTax, DELTA, "Medicare tax should round to 2.19");
        assertEquals(9.36, result.employerSocialSecurityTax, DELTA, "Employer social security tax should round to 9.36");
        assertEquals(2.19, result.employerMedicareTax, DELTA, "Employer medicare tax should round to 2.19");
        assertEquals(50.00, result.medicalDeduction, DELTA, "Medical deduction should round to 50.00");
        assertEquals(45.00, result.dependentStipend, DELTA, "Dependent stipend should round to 45.00");
        assertEquals(120.66, result.netPay, DELTA, "Net pay should round to 120.66");
    }

    @Test
    void testExactValues() {
        PayrollResult result = new PayrollCalculator.PayrollResult();
        result.regularPay = 100.50;
        result.overtimePay = 50.50;
        result.grossPay = 151.00;
        result.stateTax = 4.75;
        result.federalTax = 11.55;
        result.socialSecurityTax = 9.35;
        result.medicareTax = 2.20;
        result.employerSocialSecurityTax = 9.35;
        result.employerMedicareTax = 2.20;
        result.medicalDeduction = 50.00;
        result.dependentStipend = 45.00;
        result.netPay = 120.50;

        visitor.visit(result);

        assertEquals(100.50, result.regularPay, DELTA, "Regular pay should remain 100.50");
        assertEquals(50.50, result.overtimePay, DELTA, "Overtime pay should remain 50.50");
        assertEquals(151.00, result.grossPay, DELTA, "Gross pay should remain 151.00");
        assertEquals(4.75, result.stateTax, DELTA, "State tax should remain 4.75");
        assertEquals(11.55, result.federalTax, DELTA, "Federal tax should remain 11.55");
        assertEquals(9.35, result.socialSecurityTax, DELTA, "Social security tax should remain 9.35");
        assertEquals(2.20, result.medicareTax, DELTA, "Medicare tax should remain 2.20");
        assertEquals(9.35, result.employerSocialSecurityTax, DELTA, "Employer social security tax should remain 9.35");
        assertEquals(2.20, result.employerMedicareTax, DELTA, "Employer medicare tax should remain 2.20");
        assertEquals(50.00, result.medicalDeduction, DELTA, "Medical deduction should remain 50.00");
        assertEquals(45.00, result.dependentStipend, DELTA, "Dependent stipend should remain 45.00");
        assertEquals(120.50, result.netPay, DELTA, "Net pay should remain 120.50");
    }
} 