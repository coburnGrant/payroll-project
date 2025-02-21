package grant.coburn.view;

import java.text.NumberFormat;
import java.util.Locale;

import grant.coburn.dao.EmployeeDAO;
import grant.coburn.model.Employee;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class EmployeeManagementView extends BorderPane {
    private TableView<Employee> employeeTable;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button backButton;
    private Runnable onBack;
    private final EmployeeDAO employeeDAO = EmployeeDAO.shared;
    private Stage stage;

    public EmployeeManagementView(Stage stage) {
        this.stage = stage;
        setupUI();
        loadEmployees();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void setupUI() {
        // Create the table
        employeeTable = new TableView<>();
        setupTable();

        // Create buttons
        addButton = new Button("Add Employee");
        editButton = new Button("Edit Employee");
        deleteButton = new Button("Delete Employee");
        backButton = new Button("Back to Dashboard");

        // Button actions
        addButton.setOnAction(e -> handleAddEmployee());
        editButton.setOnAction(e -> handleEditEmployee());
        deleteButton.setOnAction(e -> handleDeleteEmployee());
        backButton.setOnAction(e -> handleBack());

        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, backButton);

        // Layout
        this.setCenter(employeeTable);
        this.setBottom(buttonBox);
        this.setPadding(new Insets(10));
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
            showAlert("Please select an employee to edit");
        }
    }

    private void handleDeleteEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (employeeDAO.deleteEmployee(selected.getEmployeeId())) {
                loadEmployees();
            } else {
                showAlert("Failed to delete employee");
            }
        } else {
            showAlert("Please select an employee to delete");
        }
    }

    private void handleBack() {
        if (onBack != null) {
            onBack.run();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadEmployees() {
        ObservableList<Employee> employees = employeeDAO.getAllEmployees();
        employeeTable.setItems(employees);
    }

    private void saveNewEmployee(Employee employee) {
        if (employeeDAO.createEmployee(employee)) {
            loadEmployees();
            stage.getScene().setRoot(this);
        } else {
            showAlert("Failed to create employee");
        }
    }

    private void updateEmployee(Employee employee) {
        if (employeeDAO.updateEmployee(employee)) {
            loadEmployees();
            stage.getScene().setRoot(this);
        } else {
            showAlert("Failed to update employee");
        }
    }
} 