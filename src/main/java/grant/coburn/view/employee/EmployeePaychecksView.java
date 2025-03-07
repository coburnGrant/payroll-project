package grant.coburn.view.employee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import grant.coburn.dao.PayrollRecordDAO;
import grant.coburn.model.Employee;
import grant.coburn.model.PayrollRecord;
import grant.coburn.view.PaycheckView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmployeePaychecksView extends VBox {
    private final Employee employee;
    private final PayrollRecordDAO payrollDAO;
    private final TableView<PayrollRecord> paychecksTable;

    public EmployeePaychecksView(Employee employee) {
        this.employee = employee;
        this.payrollDAO = PayrollRecordDAO.shared;
        this.paychecksTable = new TableView<>();
        
        setupUI();
        loadPaychecks();
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        // Title
        Text title = new Text("My Paychecks");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Setup table
        setupTable();

        // Add components
        this.getChildren().addAll(
            title,
            paychecksTable
        );
    }

    private void setupTable() {
        // Pay Period Column
        TableColumn<PayrollRecord, LocalDate> payPeriodColumn = new TableColumn<>("Pay Period");
        payPeriodColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getPayPeriodStart()));
        payPeriodColumn.setCellFactory(column -> new TableCell<PayrollRecord, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    PayrollRecord record = getTableView().getItems().get(getIndex());
                    setText(String.format("%s - %s",
                        record.getPayPeriodStart().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        record.getPayPeriodEnd().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                    ));
                }
            }
        });

        // Gross Pay Column
        TableColumn<PayrollRecord, Double> grossPayColumn = new TableColumn<>("Gross Pay");
        grossPayColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getGrossPay()).asObject());
        grossPayColumn.setCellFactory(column -> new TableCell<PayrollRecord, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", amount));
                }
            }
        });

        // Net Pay Column
        TableColumn<PayrollRecord, Double> netPayColumn = new TableColumn<>("Net Pay");
        netPayColumn.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getNetPay()).asObject());
        netPayColumn.setCellFactory(column -> new TableCell<PayrollRecord, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.2f", amount));
                }
            }
        });

        // View Button Column
        TableColumn<PayrollRecord, Void> viewColumn = new TableColumn<>("");
        viewColumn.setCellFactory(column -> new TableCell<PayrollRecord, Void>() {
            private final Button viewButton = new Button("View");
            {
                viewButton.setOnAction(event -> {
                    PayrollRecord record = getTableView().getItems().get(getIndex());
                    showPaycheckDetails(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        // Configure table
        paychecksTable.getColumns().addAll(payPeriodColumn, grossPayColumn, netPayColumn, viewColumn);
        paychecksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadPaychecks() {
        List<PayrollRecord> paychecks = payrollDAO.getPayrollRecordsByEmployee(employee.getEmployeeId());
        paychecksTable.getItems().setAll(paychecks);
    }

    private void showPaycheckDetails(PayrollRecord record) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Paycheck Details");

        PaycheckView paycheckView = new PaycheckView(stage, employee, record, stage::close);
        Scene scene = new Scene(paycheckView);
        stage.setScene(scene);
        stage.showAndWait();
    }
} 