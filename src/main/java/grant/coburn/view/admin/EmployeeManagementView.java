package grant.coburn.view.admin;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import grant.coburn.dao.EmployeeDAO;
import grant.coburn.model.Employee;
import grant.coburn.view.employee.TimeSheetView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EmployeeManagementView extends BorderPane {
    private TableView<Employee> employeeTable;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button backButton;
    private Button viewTimeSheetButton;
    private Runnable onBack;
    private final EmployeeDAO employeeDAO = EmployeeDAO.shared;
    private final Stage stage;

    public EmployeeManagementView(Stage stage) {
        this.stage = stage;
        setupUI();
        loadEmployees();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void setupUI() {
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: -fx-grey-100;");

        // Title section
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("Employee Management");
        title.getStyleClass().add("title");
        titleBox.getChildren().add(title);
        this.setTop(titleBox);

        // Create the table
        VBox tableCard = new VBox(20);
        tableCard.getStyleClass().add("card");
        tableCard.setPadding(new Insets(20));
        
        employeeTable = new TableView<>();
        setupTable();
        tableCard.getChildren().add(employeeTable);
        
        this.setCenter(tableCard);

        // Create buttons
        addButton = new Button("Add Employee");
        editButton = new Button("Edit Employee");
        deleteButton = new Button("Delete Employee");
        backButton = new Button("Back to Dashboard");
        backButton.getStyleClass().add("button-secondary");
        viewTimeSheetButton = new Button("View Time Sheet");
        viewTimeSheetButton.setDisable(true);

        //NOTE - This button is used to add test employees to the system. It is not used in the final product.
        // Add test employees button
        // Button addTestEmployeesButton = new Button("Add Test Employees");
        // addTestEmployeesButton.getStyleClass().add("button-secondary");
        // addTestEmployeesButton.setOnAction(e -> handleAddTestEmployees());

        // Button actions
        addButton.setOnAction(e -> handleAddEmployee());
        editButton.setOnAction(e -> handleEditEmployee());
        deleteButton.setOnAction(e -> handleDeleteEmployee());
        backButton.setOnAction(e -> handleBack());
        viewTimeSheetButton.setOnAction(e -> viewSelectedEmployeeTimeSheet());

        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(
            // addTestEmployeesButton,  // Will be used to add test employees to the system. Not used in the final product. 
            addButton, 
            editButton, 
            deleteButton, 
            viewTimeSheetButton, 
            backButton
        );

        this.setBottom(buttonBox);

        setupTableSelection();
    }

    private void setupTable() {
        // Create columns
        TableColumn<Employee, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmployeeId()));

        TableColumn<Employee, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDepartment()));

        TableColumn<Employee, String> jobTitleCol = new TableColumn<>("Job Title");
        jobTitleCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getJobTitle()));

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFullName()));

        TableColumn<Employee, String> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateOfBirth().toString()));

        TableColumn<Employee, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        TableColumn<Employee, String> hireDateCol = new TableColumn<>("Hire Date");
        hireDateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getHireDate().toString()));

        TableColumn<Employee, String> payTypeCol = new TableColumn<>("Pay Type");
        payTypeCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPayType().toString()));

        TableColumn<Employee, String> salaryCol = new TableColumn<>("Base Salary");
        salaryCol.setCellValueFactory(cellData -> {
            Double salary = cellData.getValue().getBaseSalary();
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
            return new SimpleStringProperty(currencyFormatter.format(salary));
        });

        TableColumn<Employee, String> medicalCol = new TableColumn<>("Medical Coverage");
        medicalCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMedicalCoverage().toString()));

        TableColumn<Employee, String> dependentsCol = new TableColumn<>("Dependents");
        dependentsCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDependentsCount().toString()));

        employeeTable.getColumns().addAll(
            idCol, deptCol, jobTitleCol, nameCol, dobCol, statusCol, hireDateCol, 
            payTypeCol, salaryCol, medicalCol, dependentsCol
        );
    }

    private void setupTableSelection() {
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
            viewTimeSheetButton.setDisable(!hasSelection);
        });
    }

    private void handleAddEmployee() {
        EmployeeFormView formView = new EmployeeFormView(
            null,
            this::saveNewEmployee,
            () -> stage.getScene().setRoot(this)
        );
        stage.getScene().setRoot(formView);
    }

    private void handleEditEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            EmployeeFormView formView = new EmployeeFormView(
                selected,
                this::updateEmployee,
                () -> stage.getScene().setRoot(this)
            );
            stage.getScene().setRoot(formView);
        } else {
            showAlert("Warning", "Please select an employee to edit");
        }
    }

    private void handleDeleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Warning", "Please select an employee to delete");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delete Employee");
        dialog.setHeaderText("Choose Delete Option");

        // Create custom buttons
        ButtonType softDeleteButton = new ButtonType("Soft Delete (Inactive)", ButtonBar.ButtonData.LEFT);
        ButtonType hardDeleteButton = new ButtonType("Hard Delete (Permanent)", ButtonBar.ButtonData.RIGHT);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(softDeleteButton, hardDeleteButton, cancelButton);

        // Add explanation and warning text
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label explanationLabel = new Label(
            "Choose how to delete employee: " + selectedEmployee.getFullName()
        );
        explanationLabel.setStyle("-fx-font-weight: bold");
        
        content.getChildren().addAll(
            explanationLabel,
            new Label("Soft Delete: Marks the employee as inactive but preserves all records."),
            new Label("Hard Delete: Permanently removes the employee and all associated records.")
        );
        
        // Add warning for hard delete
        Label warningLabel = new Label("WARNING: Hard delete cannot be undone!");
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold");
        content.getChildren().add(warningLabel);

        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == softDeleteButton) {
                // Confirm soft delete
                if (confirmDelete("Confirm Soft Delete", 
                    "Are you sure you want to mark " + selectedEmployee.getFullName() + " as inactive?")) {
                    processDeleteEmployee(selectedEmployee.getEmployeeId(), false);
                }
            } else if (result.get() == hardDeleteButton) {
                // Double confirm hard delete
                if (confirmDelete("Confirm Hard Delete", 
                    "WARNING: This will permanently delete " + selectedEmployee.getFullName() + 
                    " and all associated records!\n\n" +
                    "This includes:\n" +
                    "- All payroll records\n" +
                    "- All time entries\n" +
                    "- User account\n" +
                    "- Employee information\n\n" +
                    "This action cannot be undone.\n\n" +
                    "Are you absolutely sure you want to proceed?")) {
                    processDeleteEmployee(selectedEmployee.getEmployeeId(), true);
                }
            }
        }
    }

    private boolean confirmDelete(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void processDeleteEmployee(String employeeId, boolean hardDelete) {
        try {
            boolean success = employeeDAO.deleteEmployee(employeeId, hardDelete);
            if (success) {
                showAlert("Success", hardDelete ? 
                    "Employee has been permanently deleted." : 
                    "Employee has been marked as inactive.");
                loadEmployees();
            } else {
                showAlert("Error", "Failed to delete employee");
            }
        } catch (Exception e) {
            showAlert("Error", "Error deleting employee: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveNewEmployee(Employee employee) {
        EmployeeDAO.EmployeeCredentials credentials = employeeDAO.createEmployee(employee);
        if (credentials != null) {
            showCredentialsAlert(credentials);
            loadEmployees();
            stage.getScene().setRoot(this);
        } else {
            showAlert("Error", "Failed to create employee");
        }
    }

    private void updateEmployee(Employee employee) {
        if (employeeDAO.updateEmployee(employee)) {
            loadEmployees();
            stage.getScene().setRoot(this);
        } else {
            showAlert("Error", "Failed to update employee");
        }
    }

    private void loadEmployees() {
        ObservableList<Employee> employees = FXCollections.observableArrayList(employeeDAO.getAllEmployees());
        employeeTable.setItems(employees);
    }

    private void viewSelectedEmployeeTimeSheet() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            Stage timeSheetStage = new Stage();
            timeSheetStage.initOwner(stage);
            
            TimeSheetView timeSheetView = new TimeSheetView(selectedEmployee, timeSheetStage);
            timeSheetView.setOnBack(() -> timeSheetStage.close());
            
            javafx.scene.Scene scene = new javafx.scene.Scene(timeSheetView, 800, 600);
            scene.getStylesheets().addAll(stage.getScene().getStylesheets());
            
            timeSheetStage.setTitle("Time Sheet - " + selectedEmployee.getFullName());
            timeSheetStage.setScene(scene);
            timeSheetStage.show();
        }
    }

    private void handleBack() {
        if (onBack != null) {
            onBack.run();
        }
    }

    private void showCredentialsAlert(EmployeeDAO.EmployeeCredentials credentials) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Employee Created Successfully");
        dialog.setHeaderText("Login Credentials for " + credentials.fullName);

        TextArea textArea = new TextArea(String.format(
            "User ID: %s\n" +
            "Temporary Password: %s\n\n" +
            "Please provide these credentials to the employee.\n" +
            "They will be required to change their password on first login.",
            credentials.userId, credentials.password));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(6);
        textArea.setPrefColumnCount(30);

        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private void handleAddTestEmployees() {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Add Test Employees");
        confirmAlert.setHeaderText("Add Test Employee Data");
        confirmAlert.setContentText("This will add 12 test employees to the system.\n" +
                                  "Would you like to proceed?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                employeeDAO.insertTestEmployees();
                showAlert("Success", "Test employees have been added successfully.");
                loadEmployees();
            } catch (Exception e) {
                showAlert("Error", "Failed to add test employees: " + e.getMessage());
            }
        }
    }
} 