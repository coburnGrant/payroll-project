package grant.coburn.view;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;
import grant.coburn.util.PayrollCalculator;
import grant.coburn.util.PayrollCalculator.PayrollResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TimeEntryView extends VBox {
    private final Employee employee;
    private DatePicker datePicker;
    private TextField hoursField;
    private CheckBox ptoCheckBox;
    private Label regularHoursLabel;
    private Label overtimeHoursLabel;
    private Label grossPayLabel;
    private Label netPayLabel;
    private Label stateTaxLabel;
    private Label federalTaxLabel;
    private Label socialSecurityLabel;
    private Label medicareLabel;
    private Label medicalDeductionLabel;
    private Label dependentStipendLabel;
    private Button saveButton;
    private Button backButton;
    private Runnable onBack;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public TimeEntryView(Employee employee) {
        this.employee = employee;
        setupUI();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text("Time Entry");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create a VBox for all content except title
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(10));

        // Employee info section
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER);
        Text nameText = new Text("Employee: " + employee.getFullName());
        Text payTypeText = new Text("Pay Type: " + employee.getPayType());
        infoBox.getChildren().addAll(nameText, payTypeText);

        // Time entry form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;

        // Date picker
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setOnAction(e -> updateCalculations());
        addFormField(grid, "Date:", datePicker, row++);

        // Hours field (always disabled for salary unless PTO)
        hoursField = new TextField();
        hoursField.setPromptText("Enter hours");
        hoursField.setDisable(employee.getPayType() == Employee.PayType.SALARY);
        hoursField.textProperty().addListener((obs, old, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                hoursField.setText(old);
            } else {
                updateCalculations();
            }
        });
        addFormField(grid, "Hours:", hoursField, row++);

        // PTO checkbox
        ptoCheckBox = new CheckBox("PTO");
        ptoCheckBox.setOnAction(e -> {
            boolean isSalary = employee.getPayType() == Employee.PayType.SALARY;
            hoursField.setDisable(isSalary && !ptoCheckBox.isSelected());
            if (isSalary && !ptoCheckBox.isSelected()) {
                hoursField.clear();  // Clear hours when disabled
            }
            updateCalculations();
        });
        grid.add(ptoCheckBox, 1, row++);

        // Results section
        regularHoursLabel = new Label("Regular Hours: 0.0");
        overtimeHoursLabel = new Label("Overtime Hours: 0.0");
        grossPayLabel = new Label("Gross Pay: $0.00");
        netPayLabel = new Label("Net Pay: $0.00");
        stateTaxLabel = new Label("State Tax (3.15%): $0.00");
        federalTaxLabel = new Label("Federal Tax (7.65%): $0.00");
        socialSecurityLabel = new Label("Social Security (6.2%): $0.00");
        medicareLabel = new Label("Medicare (1.45%): $0.00");
        medicalDeductionLabel = new Label("Medical Deduction: $0.00");
        dependentStipendLabel = new Label("Dependent Stipend: $0.00");

        VBox resultsBox = new VBox(10);
        resultsBox.setAlignment(Pos.CENTER_LEFT);
        resultsBox.getChildren().addAll(
            regularHoursLabel, overtimeHoursLabel,
            new Separator(),
            grossPayLabel,
            stateTaxLabel, federalTaxLabel,
            socialSecurityLabel, medicareLabel,
            medicalDeductionLabel, dependentStipendLabel,
            new Separator(),
            netPayLabel
        );

        // Buttons
        saveButton = new Button("Save Entry");
        backButton = new Button("Back");
        saveButton.setOnAction(e -> handleSave());
        backButton.setOnAction(e -> handleBack());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(saveButton, backButton);

        // Add all components to content VBox
        content.getChildren().addAll(
            infoBox,
            grid,
            resultsBox,
            buttonBox
        );

        // Create ScrollPane and add content
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Add title and scrollPane to main VBox
        this.getChildren().addAll(title, scrollPane);

        // Initial calculations
        updateCalculations();
    }

    private void addFormField(GridPane grid, String label, Control field, int row) {
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
    }

    private void updateCalculations() {
        try {
            double hours = hoursField.getText().isEmpty() ? 0.0 : Double.parseDouble(hoursField.getText());
            TimeEntry entry = new TimeEntry(
                employee.getEmployeeId(),
                datePicker.getValue(),
                hours,
                ptoCheckBox.isSelected()
            );

            // Update hours display
            regularHoursLabel.setText(String.format("Regular Hours: %.1f", entry.getRegularHours()));
            overtimeHoursLabel.setText(String.format("Overtime Hours: %.1f", entry.getOvertimeHours()));

            // Calculate payroll
            PayrollResult result = PayrollCalculator.calculatePayroll(
                employee,
                java.util.Arrays.asList(entry),
                employee.getBaseSalary() / 40.0  // Convert weekly salary to hourly rate
            );

            // Update financial displays
            grossPayLabel.setText("Gross Pay: " + currencyFormat.format(result.grossPay));
            netPayLabel.setText("Net Pay: " + currencyFormat.format(result.netPay));
            stateTaxLabel.setText("State Tax (3.15%): " + currencyFormat.format(result.stateTax));
            federalTaxLabel.setText("Federal Tax (7.65%): " + currencyFormat.format(result.federalTax));
            socialSecurityLabel.setText("Social Security (6.2%): " + currencyFormat.format(result.socialSecurityTax));
            medicareLabel.setText("Medicare (1.45%): " + currencyFormat.format(result.medicareTax));
            medicalDeductionLabel.setText("Medical Deduction: " + currencyFormat.format(result.medicalDeduction));
            dependentStipendLabel.setText("Dependent Stipend: " + currencyFormat.format(result.dependentStipend));

        } catch (NumberFormatException e) {
            // Invalid number format - ignore
        }
    }

    private void handleSave() {
        // TODO: Save time entry to database
        showSuccess("Time entry saved successfully!");
    }

    private void handleBack() {
        if (onBack != null) {
            onBack.run();
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 