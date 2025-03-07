package grant.coburn.view;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import grant.coburn.model.Employee;
import grant.coburn.model.PayrollRecord;
import grant.coburn.util.PayrollCalculator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PaycheckView extends VBox {
    private final Stage stage;
    private final Runnable onClose;

    public PaycheckView(Stage stage, Employee employee, PayrollRecord record, Runnable onClose) {
        this.stage = stage;
        this.onClose = onClose;
        setupUI(employee, record);
    }

    private void setupUI(Employee employee, PayrollRecord record) {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        // Title
        Text title = new Text("Paycheck Details");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Employee info
        VBox employeeInfo = new VBox(5);
        employeeInfo.setAlignment(Pos.CENTER_LEFT);
        employeeInfo.getChildren().addAll(
            new Text("Employee: " + employee.getFullName()),
            new Text("Employee ID: " + employee.getEmployeeId()),
            new Text("Pay Period: " + record.getPayPeriodStart().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " + record.getPayPeriodEnd().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
        );

        // Earnings
        VBox earnings = new VBox(5);
        earnings.setAlignment(Pos.CENTER_LEFT);
        
        if (employee.getPayType() == Employee.PayType.SALARY) {
            earnings.getChildren().addAll(
                new Text("Earnings:"),
                new Text(formatSimpleLine("Regular Pay", record.getGrossPay() - record.getOvertimePay())),
                new Text(formatSimpleLine("Overtime Pay", record.getOvertimePay())),
                new Text(formatSimpleLine("Gross Pay", record.getGrossPay()))
            );
        } else {
            // Calculate hours worked from gross pay and hourly rate
            double hoursWorked = (record.getGrossPay() - record.getOvertimePay()) / employee.getBaseSalary();
            earnings.getChildren().addAll(
                new Text("Earnings:"),
                new Text(String.format(
                    "Regular Pay: %s/hr × %.1f hrs = %s", 
                    formatMoney(employee.getBaseSalary()), hoursWorked, 
                    formatMoney(record.getGrossPay() - record.getOvertimePay()
                ))),
                new Text(formatSimpleLine("Overtime Pay", record.getOvertimePay())),
                new Text(formatSimpleLine("Gross Pay", record.getGrossPay()))
            );
        }

        // Deductions
        VBox deductions = new VBox(5);
        deductions.setAlignment(Pos.CENTER_LEFT);

        deductions.getChildren().addAll(
            new Text("Deductions:"),
            new Text(formatDeductionLine("State Tax", PayrollCalculator.STATE_TAX_RATE, record.getStateTax())),
            new Text(formatDeductionLine("Federal Tax", PayrollCalculator.FEDERAL_TAX_RATE, record.getFederalTax())),
            new Text(formatDeductionLine("Social Security", PayrollCalculator.SOCIAL_SECURITY_RATE, record.getSocialSecurityTax())),
            new Text(formatDeductionLine("Medicare", PayrollCalculator.MEDICARE_RATE, record.getMedicareTax())),
            new Text(formatSimpleLine("Medical", record.getMedicalDeduction())),
            new Text(formatSimpleLine("Total Deductions", record.getTotalDeductions()))
        );

        // Benefits
        VBox benefits = new VBox(5);
        benefits.setAlignment(Pos.CENTER_LEFT);
        benefits.getChildren().addAll(
            new Text("Benefits:"),
            new Text(formatSimpleLine("Dependent Stipend", record.getDependentStipend()))
        );

        // Net Pay
        VBox netPay = new VBox(5);
        netPay.setAlignment(Pos.CENTER_LEFT);
        netPay.getChildren().addAll(
            new Text("Net Pay:"),
            new Text(formatMoney(record.getNetPay()))
        );

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            if (onClose != null) {
                onClose.run();
            }
        });

        // Add all components
        this.getChildren().addAll(
            title,
            new Separator(),
            employeeInfo,
            new Separator(),
            earnings,
            new Separator(),
            deductions,
            new Separator(),
            benefits,
            new Separator(),
            netPay,
            closeButton
        );
    }

    private static String formatMoney(double amount) {
        return String.format(Locale.US, "$%,.2f", amount);
    }

    private static String formatPercentage(double rate) {
        return String.format(Locale.US, "%.2f%%", rate * 100);
    }

    private static String formatDeductionLine(String label, double rate, double amount) {
        return String.format("%s (%s): %s", label, formatPercentage(rate), formatMoney(amount));
    }
    
    private static String formatSimpleLine(String label, double amount) {
        return String.format("%s: %s", label, formatMoney(amount));
    }
} 