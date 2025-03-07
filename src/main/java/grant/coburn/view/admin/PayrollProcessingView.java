package grant.coburn.view.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import grant.coburn.model.Employee;
import grant.coburn.model.PayrollRecord;
import grant.coburn.util.PayrollProcessingResult;
import grant.coburn.util.PayrollProcessor;
import grant.coburn.view.PaycheckView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PayrollProcessingView extends VBox {
    private final Stage stage;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TableView<PayrollRecord> payrollTable;
    private Button processButton;
    private Button viewPaycheckButton;
    private Button deleteButton;
    private Button backButton;
    private Runnable onBack;

    public PayrollProcessingView(Stage stage) {
        this.stage = stage;
        setupUI();
        // Initial table refresh
        refreshPayrollTable();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text("Payroll Processing");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Date selection
        GridPane dateGrid = new GridPane();
        dateGrid.setAlignment(Pos.CENTER);
        dateGrid.setHgap(10);
        dateGrid.setVgap(10);
        dateGrid.setPadding(new Insets(10));

        startDatePicker = new DatePicker(LocalDate.now().minusWeeks(1));
        endDatePicker = new DatePicker(LocalDate.now());

        // Add listeners to refresh table when dates change
        startDatePicker.setOnAction(e -> refreshPayrollTable());
        endDatePicker.setOnAction(e -> refreshPayrollTable());

        dateGrid.add(new Label("Pay Period Start:"), 0, 0);
        dateGrid.add(startDatePicker, 1, 0);
        dateGrid.add(new Label("Pay Period End:"), 2, 0);
        dateGrid.add(endDatePicker, 3, 0);

        // Payroll table
        setupPayrollTable();

        // Buttons
        processButton = new Button("Process Payroll");
        viewPaycheckButton = new Button("View Paycheck");
        deleteButton = new Button("Delete Record");
        backButton = new Button("Back to Dashboard");
        
        processButton.setOnAction(e -> handleProcessPayroll());
        viewPaycheckButton.setOnAction(e -> handleViewPaycheck());
        deleteButton.setOnAction(e -> handleDeleteRecord());
        viewPaycheckButton.setDisable(true);
        deleteButton.setDisable(true);
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        processButton.getStyleClass().add("button-primary");
        viewPaycheckButton.getStyleClass().add("button-primary");
        deleteButton.getStyleClass().addAll("button-secondary", "button-danger");
        backButton.getStyleClass().add("button-secondary");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(processButton, viewPaycheckButton, deleteButton, backButton);

        // Add all components to main VBox
        this.getChildren().addAll(
            title,
            new Separator(),
            dateGrid,
            new Separator(),
            new Label("Payroll Records"),
            new ScrollPane(payrollTable),
            buttonBox
        );
    }

    private void handleViewPaycheck() {
        PayrollRecord selectedRecord = payrollTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showError("Please select a payroll record to view.");
            return;
        }

        // Get employee details
        Employee employee = PayrollProcessor.shared().getEmployeeById(selectedRecord.getEmployeeId());
        if (employee == null) {
            showError("Could not find employee details.");
            return;
        }

        // Show paycheck view in a modal window
        Stage paycheckStage = new Stage();
        paycheckStage.initModality(Modality.APPLICATION_MODAL);
        paycheckStage.initOwner(stage);
        paycheckStage.setTitle("Paycheck Details");

        PaycheckView paycheckView = new PaycheckView(paycheckStage, employee, selectedRecord, paycheckStage::close);
        Scene scene = new Scene(paycheckView);
        scene.getStylesheets().addAll(stage.getScene().getStylesheets());
        
        paycheckStage.setScene(scene);
        paycheckStage.showAndWait();
    }

    private void handleDeleteRecord() {
        PayrollRecord selectedRecord = payrollTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showError("Please select a payroll record to delete.");
            return;
        }

        // Get employee details for confirmation message
        Employee employee = PayrollProcessor.shared().getEmployeeById(selectedRecord.getEmployeeId());
        String employeeName = employee != null ? employee.getFullName() : selectedRecord.getEmployeeId();

        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Payroll Record");
        alert.setContentText(String.format(
            "Are you sure you want to delete the payroll record for %s?\n\nPay Period: %s - %s\nGross Pay: $%,.2f\nNet Pay: $%,.2f",
            employeeName,
            selectedRecord.getPayPeriodStart().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
            selectedRecord.getPayPeriodEnd().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
            selectedRecord.getGrossPay(),
            selectedRecord.getNetPay()
        ));

        if (alert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            // Delete the record
            boolean success = PayrollProcessor.shared().deletePayrollRecord(selectedRecord.getEmployeeId(), selectedRecord.getPayPeriodStart(), selectedRecord.getPayPeriodEnd());
            if (success) {
                showSuccess("Payroll record deleted successfully.");
                refreshPayrollTable(); // Refresh the table to show the updated list
            } else {
                showError("Failed to delete payroll record.");
            }
        }
    }

    private void setupPayrollTable() {
        payrollTable = new TableView<>();
        payrollTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        payrollTable.setMinHeight(400);
        payrollTable.setPrefHeight(500);

        // Create columns with minimum widths
        TableColumn<PayrollRecord, String> employeeIdCol = new TableColumn<>("Employee ID");
        employeeIdCol.setMinWidth(80);
        employeeIdCol.setPrefWidth(100);
        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        // Employee Name Column
        TableColumn<PayrollRecord, String> employeeNameCol = new TableColumn<>("Employee Name");
        employeeNameCol.setMinWidth(150);
        employeeNameCol.setPrefWidth(200);
        employeeNameCol.setCellValueFactory(cellData -> {
            Employee employee = PayrollProcessor.shared().getEmployeeById(cellData.getValue().getEmployeeId());
            return new javafx.beans.property.SimpleStringProperty(
                employee != null ? employee.getFullName() : "Unknown"
            );
        });

        TableColumn<PayrollRecord, String> periodCol = new TableColumn<>("Pay Period");
        periodCol.setMinWidth(150);
        periodCol.setPrefWidth(200);
        periodCol.setCellValueFactory(cellData -> {
            PayrollRecord record = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                record.getPayPeriodStart().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " +
                record.getPayPeriodEnd().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            );
        });

        TableColumn<PayrollRecord, String> grossPayCol = new TableColumn<>("Gross Pay");
        grossPayCol.setMinWidth(100);
        grossPayCol.setPrefWidth(120);
        grossPayCol.setCellValueFactory(cellData -> {
            PayrollRecord record = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                String.format(Locale.US, "$%,.2f", record.getGrossPay())
            );
        });

        TableColumn<PayrollRecord, String> netPayCol = new TableColumn<>("Net Pay");
        netPayCol.setMinWidth(100);
        netPayCol.setPrefWidth(120);
        netPayCol.setCellValueFactory(cellData -> {
            PayrollRecord record = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                String.format(Locale.US, "$%,.2f", record.getNetPay())
            );
        });

        TableColumn<PayrollRecord, String> creationDateCol = new TableColumn<>("Created");
        creationDateCol.setMinWidth(150);
        creationDateCol.setPrefWidth(200);
        creationDateCol.setCellValueFactory(cellData -> {
            PayrollRecord record = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                record.getCreationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
            );
        });

        // Add all columns to the table
        payrollTable.getColumns().addAll(
            employeeIdCol,
            employeeNameCol,
            periodCol,
            grossPayCol,
            netPayCol,
            creationDateCol
        );

        // Make the table resizable
        payrollTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Enable/disable view paycheck and delete buttons based on selection
        payrollTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            viewPaycheckButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
    }

    private void handleProcessPayroll() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showError("Please select both start and end dates.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showError("Start date must be before end date.");
            return;
        }

        // Process payroll using the PayrollProcessor
        PayrollProcessingResult result = PayrollProcessor.shared().processPayroll(startDate, endDate);
        
        if (result.isSuccess()) {
            showSuccessDialog(
                String.format("Successfully processed payroll for %d employees", 
                result.getEmployeesProcessed())
            );
            // Refresh the table to show new records
            refreshPayrollTable();
        } else {
            StringBuilder message = new StringBuilder("Payroll processing failed:\n\n");
            
            // Add errors
            if (result.hasErrors()) {
                message.append("Errors:\n");
                for (String error : result.getErrors()) {
                    message.append("- ").append(error).append("\n");
                }
                message.append("\n");
            }
            
            // Add warnings
            if (result.hasWarnings()) {
                message.append("Warnings:\n");
                for (String warning : result.getWarnings()) {
                    message.append("- ").append(warning).append("\n");
                }
            }
            
            showErrorDialog(message.toString());
        }
    }

    private void refreshPayrollTable() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null) {
            payrollTable.getItems().clear();
            List<PayrollRecord> records = PayrollProcessor.shared().getPayrollRecords(startDate, endDate);
            payrollTable.getItems().addAll(records);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 