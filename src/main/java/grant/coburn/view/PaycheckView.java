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
            new Text("Pay Period: " + record.getPayPeriodStart().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + 
                    " - " + record.getPayPeriodEnd().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
        );

        // Earnings
        VBox earnings = new VBox(5);
        earnings.setAlignment(Pos.CENTER_LEFT);
        
        if (employee.getPayType() == Employee.PayType.SALARY) {
            // Calculate biweekly salary (annual salary / 26 pay periods)
            double biweeklySalary = employee.getBaseSalary() / 26;
            earnings.getChildren().addAll(
                new Text("Earnings:"),
                new Text(String.format(Locale.US, "Regular Pay: $%,.2f/year ÷ 26 pay periods = $%,.2f", 
                    employee.getBaseSalary(), biweeklySalary)),
                new Text(String.format(Locale.US, "Overtime Pay: $%,.2f", record.getOvertimePay())),
                new Text(String.format(Locale.US, "Gross Pay: $%,.2f", record.getGrossPay()))
            );
        } else {
            // Calculate hours worked from gross pay and hourly rate
            double hoursWorked = (record.getGrossPay() - record.getOvertimePay()) / employee.getBaseSalary();
            earnings.getChildren().addAll(
                new Text("Earnings:"),
                new Text(String.format(Locale.US, "Regular Pay: $%,.2f/hr × %.1f hrs = $%,.2f", 
                    employee.getBaseSalary(), hoursWorked, record.getGrossPay() - record.getOvertimePay())),
                new Text(String.format(Locale.US, "Overtime Pay: $%,.2f", record.getOvertimePay())),
                new Text(String.format(Locale.US, "Gross Pay: $%,.2f", record.getGrossPay()))
            );
        }

        // Deductions
        VBox deductions = new VBox(5);
        deductions.setAlignment(Pos.CENTER_LEFT);
        deductions.getChildren().addAll(
            new Text("Deductions:"),
            new Text(String.format(Locale.US, "State Tax (%.2f%%): $%,.2f", 
                PayrollCalculator.STATE_TAX_RATE * 100, record.getStateTax())),
            new Text(String.format(Locale.US, "Federal Tax (%.2f%%): $%,.2f", 
                PayrollCalculator.FEDERAL_TAX_RATE * 100, record.getFederalTax())),
            new Text(String.format(Locale.US, "Social Security (%.2f%%): $%,.2f", 
                PayrollCalculator.SOCIAL_SECURITY_RATE * 100, record.getSocialSecurityTax())),
            new Text(String.format(Locale.US, "Medicare (%.2f%%): $%,.2f", 
                PayrollCalculator.MEDICARE_RATE * 100, record.getMedicareTax())),
            new Text(String.format(Locale.US, "Medical: $%,.2f", record.getMedicalDeduction())),
            new Text(String.format(Locale.US, "Total Deductions: $%,.2f", 
                record.getStateTax() + record.getFederalTax() + record.getSocialSecurityTax() + 
                record.getMedicareTax() + record.getMedicalDeduction()))
        );

        // Benefits
        VBox benefits = new VBox(5);
        benefits.setAlignment(Pos.CENTER_LEFT);
        benefits.getChildren().addAll(
            new Text("Benefits:"),
            new Text(String.format(Locale.US, "Dependent Stipend: $%,.2f", record.getDependentStipend()))
        );

        // Net Pay
        VBox netPay = new VBox(5);
        netPay.setAlignment(Pos.CENTER_LEFT);
        netPay.getChildren().addAll(
            new Text("Net Pay:"),
            new Text(String.format(Locale.US, "$%,.2f", record.getNetPay()))
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
} 