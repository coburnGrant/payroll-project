package grant.coburn.util;

import grant.coburn.util.PayrollCalculator.PayrollResult;

public class MonetaryRoundingVisitor implements PayrollResultVisitor {
    @Override
    public void visit(PayrollResult result) {
        result.regularPay = roundToCents(result.regularPay);
        result.overtimePay = roundToCents(result.overtimePay);
        result.grossPay = roundToCents(result.grossPay);
        result.stateTax = roundToCents(result.stateTax);
        result.federalTax = roundToCents(result.federalTax);
        result.socialSecurityTax = roundToCents(result.socialSecurityTax);
        result.medicareTax = roundToCents(result.medicareTax);
        result.employerSocialSecurityTax = roundToCents(result.employerSocialSecurityTax);
        result.employerMedicareTax = roundToCents(result.employerMedicareTax);
        result.medicalDeduction = roundToCents(result.medicalDeduction);
        result.dependentStipend = roundToCents(result.dependentStipend);
        result.netPay = roundToCents(result.netPay);
    }

    private double roundToCents(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
} 