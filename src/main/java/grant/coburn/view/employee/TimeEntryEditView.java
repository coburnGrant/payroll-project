package grant.coburn.view.employee;

import java.text.NumberFormat;
import java.util.Locale;

import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;
import grant.coburn.util.PayrollCalculator;
import grant.coburn.util.PayrollCalculator.PayrollResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TimeEntryEditView extends VBox {
    private final Employee employee;
    private final TimeEntry timeEntry;
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
    private Button cancelButton;
    private Runnable onSave;
    private Runnable onCancel;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public TimeEntryEditView(Employee employee, TimeEntry timeEntry) {
        this.employee = employee;
        this.timeEntry = timeEntry;
        setupUI();
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text("Edit Time Entry");
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
        String payRateText = employee.getPayType() == Employee.PayType.SALARY ? 
            String.format("Weekly Salary: %s", currencyFormat.format(employee.getBaseSalary())) :
            String.format("Hourly Rate: %s", currencyFormat.format(employee.getBaseSalary()));
        Text rateText = new Text(payRateText);
        infoBox.getChildren().addAll(nameText, payTypeText, rateText);

        // Time entry form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;

        // Date picker
        datePicker = new DatePicker(timeEntry.getWorkDate());
        datePicker.setOnAction(e -> updateCalculations());
        addFormField(grid, "Date:", datePicker, row++);

        // Hours field (always disabled for salary unless PTO)
        hoursField = new TextField(String.format("%.2f", timeEntry.getHoursWorked()));
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
        ptoCheckBox.setSelected(timeEntry.isPto());
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
        
        // Only show medical deduction for salary employees
        if (employee.getPayType() == Employee.PayType.SALARY) {
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
        } else {
            resultsBox.getChildren().addAll(
                regularHoursLabel, overtimeHoursLabel,
                new Separator(),
                grossPayLabel,
                stateTaxLabel, federalTaxLabel,
                socialSecurityLabel, medicareLabel,
                dependentStipendLabel,
                new Separator(),
                netPayLabel
            );
        }

        // Buttons
        saveButton = new Button("Save Changes");
        cancelButton = new Button("Cancel");
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> {
            if (onCancel != null) {
                onCancel.run();
            }
            // Close the window
            this.getScene().getWindow().hide();
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        // Add all components to content VBox
        content.getChildren().addAll(
            infoBox,
            grid,
            resultsBox,
            buttonBox
        );

        // Add title and content to main VBox
        this.getChildren().addAll(title, content);

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
            PayrollResult result = PayrollCalculator.calculatePayrollPreview(
                employee,
                java.util.Arrays.asList(entry)
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
        try {
            double hours = hoursField.getText().isEmpty() ? 0.0 : Double.parseDouble(hoursField.getText());
            timeEntry.setWorkDate(datePicker.getValue());
            timeEntry.setHoursWorked(hours);
            timeEntry.setPto(ptoCheckBox.isSelected());

            // Update in database
            boolean success = grant.coburn.dao.TimeEntryDAO.shared.updateTimeEntry(timeEntry);
            
            if (success) {
                if (onSave != null) {
                    onSave.run();
                }
            } else {
                showError("Failed to update time entry. It may be locked or no longer exists.");
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number of hours.");
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 