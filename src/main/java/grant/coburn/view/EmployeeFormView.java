package grant.coburn.view;

import java.util.function.Consumer;

import grant.coburn.model.Employee;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EmployeeFormView extends VBox {
    private TextField employeeIdField;
    private TextField departmentField;
    private TextField jobTitleField;
    private TextField firstNameField;
    private TextField lastNameField;
    private ComboBox<Employee.Status> statusComboBox;
    private DatePicker hireDatePicker;
    private ComboBox<Employee.PayType> payTypeComboBox;
    private TextField baseSalaryField;
    private ComboBox<Employee.MedicalCoverage> medicalCoverageComboBox;
    private Spinner<Integer> dependentsSpinner;
    private DatePicker dateOfBirthPicker;
    private ComboBox<Employee.Gender> genderComboBox;
    private Label salaryLabel;
    
    private final Employee employeeToEdit;
    private final Consumer<Employee> onSave;
    private final Runnable onCancel;

    public EmployeeFormView(Employee employeeToEdit, Consumer<Employee> onSave, Runnable onCancel) {
        this.employeeToEdit = employeeToEdit;
        this.onSave = onSave;
        this.onCancel = onCancel;
        setupUI();
        if (employeeToEdit != null) {
            populateFields();
        }
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text(employeeToEdit == null ? "Add Employee" : "Edit Employee");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create content VBox
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        int row = 0;

        // Employee ID
        employeeIdField = new TextField();
        employeeIdField.setPromptText("EMP####");
        employeeIdField.setDisable(employeeToEdit != null); // Disable if editing
        addFormField(grid, "Employee ID:", employeeIdField, row++);

        // Department
        departmentField = new TextField();
        addFormField(grid, "Department:", departmentField, row++);

        // Job Title
        jobTitleField = new TextField();
        addFormField(grid, "Job Title:", jobTitleField, row++);

        // Name Fields
        firstNameField = new TextField();
        addFormField(grid, "First Name:", firstNameField, row++);
        
        lastNameField = new TextField();
        addFormField(grid, "Last Name:", lastNameField, row++);

        // Date of Birth
        dateOfBirthPicker = new DatePicker();
        addFormField(grid, "Date of Birth:", dateOfBirthPicker, row++);

        // Gender
        genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll(Employee.Gender.values());
        addFormField(grid, "Gender:", genderComboBox, row++);

        // Status
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(Employee.Status.values());
        addFormField(grid, "Status:", statusComboBox, row++);

        // Hire Date
        hireDatePicker = new DatePicker();
        addFormField(grid, "Hire Date:", hireDatePicker, row++);

        // Pay Type with listener
        payTypeComboBox = new ComboBox<>();
        payTypeComboBox.getItems().addAll(Employee.PayType.values());
        payTypeComboBox.setOnAction(e -> updateSalaryLabel());  // Add listener
        addFormField(grid, "Pay Type:", payTypeComboBox, row++);

        // Base Salary with dynamic label
        baseSalaryField = new TextField();
        salaryLabel = new Label("Base Salary:");  // Initialize label
        grid.add(salaryLabel, 0, row);
        grid.add(baseSalaryField, 1, row++);

        // Medical Coverage
        medicalCoverageComboBox = new ComboBox<>();
        medicalCoverageComboBox.getItems().addAll(Employee.MedicalCoverage.values());
        addFormField(grid, "Medical Coverage:", medicalCoverageComboBox, row++);

        // Dependents
        dependentsSpinner = new Spinner<>(0, 10, 0);
        addFormField(grid, "Dependents:", dependentsSpinner, row++);

        // Buttons
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        // Add grid to content
        content.getChildren().addAll(grid, buttonBox);

        // Create ScrollPane
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Add components to main VBox
        this.getChildren().addAll(title, scrollPane);
    }

    private void addFormField(GridPane grid, String label, Control field, int row) {
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
    }

    private void populateFields() {
        employeeIdField.setText(employeeToEdit.getEmployeeId());
        departmentField.setText(employeeToEdit.getDepartment());
        jobTitleField.setText(employeeToEdit.getJobTitle());
        firstNameField.setText(employeeToEdit.getFirstName());
        lastNameField.setText(employeeToEdit.getLastName());
        dateOfBirthPicker.setValue(employeeToEdit.getDateOfBirth());
        genderComboBox.setValue(employeeToEdit.getGender());
        statusComboBox.setValue(employeeToEdit.getStatus());
        hireDatePicker.setValue(employeeToEdit.getHireDate());
        payTypeComboBox.setValue(employeeToEdit.getPayType());
        updateSalaryLabel();  // Update label when populating
        baseSalaryField.setText(employeeToEdit.getBaseSalary().toString());
        medicalCoverageComboBox.setValue(employeeToEdit.getMedicalCoverage());
        dependentsSpinner.getValueFactory().setValue(employeeToEdit.getDependentsCount());
    }

    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            Employee employee = new Employee(
                employeeIdField.getText(),
                departmentField.getText(),
                jobTitleField.getText(),
                firstNameField.getText(),
                lastNameField.getText(),
                statusComboBox.getValue(),
                dateOfBirthPicker.getValue(),
                hireDatePicker.getValue(),
                payTypeComboBox.getValue(),
                Double.parseDouble(baseSalaryField.getText()),
                medicalCoverageComboBox.getValue(),
                dependentsSpinner.getValue()
            );

            // Set additional fields
            employee.setGender(genderComboBox.getValue());
            employee.setCompanyEmail(employeeIdField.getText() + "@company.com");  // Default email
            employee.setAddressLine1("");  // Empty defaults for now
            employee.setAddressLine2("");
            employee.setCity("");
            employee.setState("");
            employee.setZip("");
            employee.setPicturePath("");

            onSave.accept(employee);
        } catch (NumberFormatException e) {
            showError("Invalid salary format");
        }
    }

    private void handleCancel() {
        if (onCancel != null) {
            onCancel.run();
        }
    }

    private boolean validateInput() {
        if (employeeIdField.getText().isEmpty() || !employeeIdField.getText().matches("EMP\\d{4}")) {
            showError("Employee ID must be in format EMP####");
            return false;
        }
        if (departmentField.getText().isEmpty()) {
            showError("Department is required");
            return false;
        }
        if (jobTitleField.getText().isEmpty()) {
            showError("Job Title is required");
            return false;
        }
        if (firstNameField.getText().isEmpty()) {
            showError("First Name is required");
            return false;
        }
        if (lastNameField.getText().isEmpty()) {
            showError("Last Name is required");
            return false;
        }
        if (statusComboBox.getValue() == null) {
            showError("Status is required");
            return false;
        }
        if (hireDatePicker.getValue() == null) {
            showError("Hire Date is required");
            return false;
        }
        if (payTypeComboBox.getValue() == null) {
            showError("Pay Type is required");
            return false;
        }
        if (baseSalaryField.getText().isEmpty()) {
            showError("Base Salary is required");
            return false;
        }
        if (medicalCoverageComboBox.getValue() == null) {
            showError("Medical Coverage is required");
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            showError("Date of Birth is required");
            return false;
        }
        if (genderComboBox.getValue() == null) {
            showError("Gender is required");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateSalaryLabel() {
        String labelText = (payTypeComboBox.getValue() == Employee.PayType.HOURLY) 
            ? "Hourly Rate:" 
            : "Base Salary:";
        salaryLabel.setText(labelText);
    }
} 