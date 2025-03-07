package grant.coburn.util;

public class PayrollResultValidationVisitor implements PayrollResultVisitor {
    private StringBuilder errors = new StringBuilder();

    @Override
    public void visit(PayrollCalculator.PayrollResult result) {
        // Validate gross pay calculation
        double calculatedGrossPay = result.regularPay + result.overtimePay;
        if (Math.abs(calculatedGrossPay - result.grossPay) > 0.01) {
            errors.append("Gross pay does not match sum of regular and overtime pay\n");
        }

        // Validate net pay calculation
        double calculatedNetPay = result.grossPay 
            - result.stateTax 
            - result.federalTax 
            - result.socialSecurityTax 
            - result.medicareTax 
            - result.medicalDeduction 
            + result.dependentStipend;
        if (Math.abs(calculatedNetPay - result.netPay) > 0.01) {
            errors.append("Net pay calculation is incorrect\n");
        }

        // Validate tax calculations
        validateTaxRate(result.stateTax, result.grossPay, PayrollCalculator.STATE_TAX_RATE, "State tax");
        validateTaxRate(result.federalTax, result.grossPay, PayrollCalculator.FEDERAL_TAX_RATE, "Federal tax");
        validateTaxRate(result.socialSecurityTax, result.grossPay, PayrollCalculator.SOCIAL_SECURITY_RATE, "Social Security tax");
        validateTaxRate(result.medicareTax, result.grossPay, PayrollCalculator.MEDICARE_RATE, "Medicare tax");

        // Validate employer portions match employee portions
        if (Math.abs(result.socialSecurityTax - result.employerSocialSecurityTax) > 0.01) {
            errors.append("Employer Social Security tax does not match employee portion\n");
        }
        if (Math.abs(result.medicareTax - result.employerMedicareTax) > 0.01) {
            errors.append("Employer Medicare tax does not match employee portion\n");
        }

        // Validate pay period dates
        if (result.payPeriodStart != null && result.payPeriodEnd != null) {
            if (result.payPeriodEnd.isBefore(result.payPeriodStart)) {
                errors.append("Pay period end date is before start date\n");
            }
        }

        // Validate no negative values
        validateNonNegative(result.regularPay, "Regular pay");
        validateNonNegative(result.overtimePay, "Overtime pay");
        validateNonNegative(result.grossPay, "Gross pay");
        validateNonNegative(result.netPay, "Net pay");
        validateNonNegative(result.stateTax, "State tax");
        validateNonNegative(result.federalTax, "Federal tax");
        validateNonNegative(result.socialSecurityTax, "Social Security tax");
        validateNonNegative(result.medicareTax, "Medicare tax");
        validateNonNegative(result.medicalDeduction, "Medical deduction");
        validateNonNegative(result.dependentStipend, "Dependent stipend");
    }

    private void validateTaxRate(double tax, double grossPay, double expectedRate, String taxName) {
        if (grossPay > 0) {
            double actualRate = tax / grossPay;
            if (Math.abs(actualRate - expectedRate) > 0.0001) {
                errors.append(String.format("%s rate is incorrect (expected %.4f, got %.4f)\n", 
                    taxName, expectedRate, actualRate));
            }
        }
    }

    private void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            errors.append(String.format("%s cannot be negative (got %.2f)\n", fieldName, value));
        }
    }

    public boolean hasErrors() {
        return errors.length() > 0;
    }

    public String getErrors() {
        return errors.toString();
    }
} 