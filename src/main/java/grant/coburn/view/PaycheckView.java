package grant.coburn.view;

import java.time.format.DateTimeFormatter;

import grant.coburn.model.Employee;
import grant.coburn.model.PayrollRecord;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PaycheckView extends VBox {
    private final Stage stage;
    private final Runnable onClose;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public PaycheckView(Stage stage, Employee employee, PayrollRecord record, Runnable onClose) {
        this.stage = stage;
        this.onClose = onClose;
        setupUI(employee, record);
    }

    private void setupUI(Employee employee, PayrollRecord record) {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(40));
        this.setSpacing(30);
        this.setStyle("-fx-background-color: -fx-grey-100;");

        // Title section
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("Paycheck Details");
        title.getStyleClass().add("title");
        Text subtitle = new Text(String.format("%s - Pay Period: %s to %s",
            employee.getFullName(),
            record.getPayPeriodStart().format(DATE_FORMATTER),
            record.getPayPeriodEnd().format(DATE_FORMATTER)));
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        // Main content card
        VBox contentCard = new VBox(20);
        contentCard.getStyleClass().add("card");
        contentCard.setPadding(new Insets(30));
        contentCard.setMaxWidth(600);

        // Earnings section
        Text earningsTitle = new Text("Earnings");
        earningsTitle.getStyleClass().add("subtitle");
        earningsTitle.setStyle("-fx-font-weight: bold;");
        
        VBox earningsBox = new VBox(10);
        earningsBox.getChildren().addAll(
            createDetailRow("Regular Pay:", formatMoney(record.getGrossPay() - record.getOvertimePay())),
            createDetailRow("Overtime Pay:", formatMoney(record.getOvertimePay())),
            createDetailRow("Gross Pay:", formatMoney(record.getGrossPay()))
        );

        // Deductions section
        Text deductionsTitle = new Text("Deductions");
        deductionsTitle.getStyleClass().add("subtitle");
        deductionsTitle.setStyle("-fx-font-weight: bold;");
        
        VBox deductionsBox = new VBox(10);
        deductionsBox.getChildren().addAll(
            createDetailRow("Federal Tax:", formatMoney(record.getFederalTax())),
            createDetailRow("State Tax:", formatMoney(record.getStateTax())),
            createDetailRow("Social Security:", formatMoney(record.getSocialSecurityTax())),
            createDetailRow("Medicare:", formatMoney(record.getMedicareTax())),
            createDetailRow("Medical Insurance:", formatMoney(record.getMedicalDeduction()))
        );

        // Calculate total deductions
        double totalDeductions = record.getFederalTax() + 
                               record.getStateTax() + 
                               record.getSocialSecurityTax() + 
                               record.getMedicareTax() + 
                               record.getMedicalDeduction();

        // Total deductions row
        HBox totalDeductionsBox = createDetailRow("Total Deductions:", formatMoney(totalDeductions));
        totalDeductionsBox.setStyle("-fx-font-weight: bold;");

        // Summary section
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        HBox totalBox = createDetailRow("Net Pay:", formatMoney(record.getNetPay()));
        totalBox.setStyle("-fx-font-weight: bold;");

        // Add all sections to content card
        contentCard.getChildren().addAll(
            earningsTitle,
            earningsBox,
            new Separator(),
            deductionsTitle,
            deductionsBox,
            new Separator(),
            totalDeductionsBox,
            separator,
            totalBox
        );

        // Button section
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("button-secondary");
        closeButton.setOnAction(e -> {
            if (onClose != null) {
                onClose.run();
            }
            stage.close();
        });

        // Add everything to main container
        this.getChildren().addAll(titleBox, contentCard, closeButton);
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_RIGHT);
        Label labelText = new Label(label);
        labelText.setMinWidth(150);
        Label valueText = new Label(value);
        valueText.setMinWidth(100);
        row.getChildren().addAll(labelText, valueText);
        return row;
    }

    private String formatMoney(double amount) {
        return String.format("$%,.2f", amount);
    }
} 