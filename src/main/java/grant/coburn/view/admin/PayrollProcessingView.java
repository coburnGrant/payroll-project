package grant.coburn.view.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import grant.coburn.model.Employee;
import grant.coburn.model.PayrollRecord;
import grant.coburn.util.PayrollProcessor;
import grant.coburn.view.PaycheckView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.stage.Stage;

public class PayrollProcessingView extends VBox {
    private final Stage stage;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TableView<PayrollRecord> payrollTable;
    private Button processButton;
    private Button viewPaycheckButton;
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
        backButton = new Button("Back");
        
        processButton.setOnAction(e -> handleProcessPayroll());
        viewPaycheckButton.setOnAction(e -> handleViewPaycheck());
        viewPaycheckButton.setDisable(true);
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(processButton, viewPaycheckButton, backButton);

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

        // Show paycheck view
        PaycheckView paycheckView = new PaycheckView(stage, employee, selectedRecord, () -> {
            stage.getScene().setRoot(this);
        });
        stage.getScene().setRoot(paycheckView);
    }

    private void setupPayrollTable() {
        payrollTable = new TableView<>();
        payrollTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        payrollTable.setMinHeight(400);
        payrollTable.setPrefHeight(500);

        // Create columns with minimum widths
        TableColumn<PayrollRecord, String> employeeIdCol = new TableColumn<>("Employee ID");
        employeeIdCol.setMinWidth(100);
        employeeIdCol.setPrefWidth(120);
        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

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
            employeeIdCol, periodCol, grossPayCol, netPayCol, creationDateCol
        );

        // Make the table resizable
        payrollTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Enable/disable view paycheck button based on selection
        payrollTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewPaycheckButton.setDisable(newSelection == null);
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
        boolean success = PayrollProcessor.shared().processPayroll(startDate, endDate);
        
        if (success) {
            // Refresh table
            refreshPayrollTable();
            showSuccess("Payroll processed successfully!");
        } else {
            showError("Failed to process payroll. Please check your data and try again.");
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
} 