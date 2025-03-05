package grant.coburn.view.employee;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import grant.coburn.dao.TimeEntryDAO;
import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TimeSheetView extends BorderPane {
    private final Employee employee;
    private final Stage stage;
    private TableView<TimeEntry> timeEntryTable;
    private Button editButton;
    private Button deleteButton;
    private Button backButton;
    private Runnable onBack;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public TimeSheetView(Employee employee, Stage stage) {
        this.employee = employee;
        this.stage = stage;
        setupUI();
        loadTimeEntries();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void setupUI() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: white;");

        // Title
        Text title = new Text("Time Sheet - " + employee.getFullName());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        this.setTop(titleBox);

        // Table setup
        timeEntryTable = new TableView<>();
        timeEntryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Date column
        TableColumn<TimeEntry, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        dateColumn.setCellFactory(col -> new TableCell<TimeEntry, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(date.toString());
                }
            }
        });

        // Hours column
        TableColumn<TimeEntry, Double> hoursColumn = new TableColumn<>("Hours");
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hoursWorked"));
        hoursColumn.setCellFactory(col -> new TableCell<TimeEntry, Double>() {
            @Override
            protected void updateItem(Double hours, boolean empty) {
                super.updateItem(hours, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", hours));
                }
            }
        });

        // PTO column
        TableColumn<TimeEntry, Boolean> ptoColumn = new TableColumn<>("PTO");
        ptoColumn.setCellValueFactory(new PropertyValueFactory<>("pto"));
        ptoColumn.setCellFactory(col -> new TableCell<TimeEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean isPto, boolean empty) {
                super.updateItem(isPto, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(isPto ? "Yes" : "No");
                }
            }
        });

        // Status column
        TableColumn<TimeEntry, Boolean> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("locked"));
        statusColumn.setCellFactory(col -> new TableCell<TimeEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean isLocked, boolean empty) {
                super.updateItem(isLocked, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(isLocked ? "Locked" : "Editable");
                }
            }
        });

        timeEntryTable.getColumns().addAll(dateColumn, hoursColumn, ptoColumn, statusColumn);

        // Buttons
        editButton = new Button("Edit Entry");
        deleteButton = new Button("Delete Entry");
        backButton = new Button("Back");

        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        // Disable edit/delete buttons until an entry is selected
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(editButton, deleteButton, backButton);

        // Enable/disable buttons based on selection
        timeEntryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            boolean isEditable = hasSelection && !newSelection.isLocked();
            editButton.setDisable(!isEditable);
            deleteButton.setDisable(!isEditable);
        });

        // Layout
        this.setCenter(timeEntryTable);
        this.setBottom(buttonBox);
    }

    private void loadTimeEntries() {
        timeEntryTable.getItems().clear();
        timeEntryTable.getItems().addAll(TimeEntryDAO.shared().getTimeEntriesByEmployeeId(employee.getEmployeeId()));
    }

    private void handleEdit() {
        TimeEntry selectedEntry = timeEntryTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null && !selectedEntry.isLocked()) {
            TimeEntryEditView editView = new TimeEntryEditView(employee, selectedEntry);
            editView.setOnSave(() -> {
                loadTimeEntries();
                showSuccess("Time entry updated successfully!");
            });
            editView.setOnCancel(() -> stage.hide());
            
            Stage editStage = new Stage();
            editStage.initOwner(stage);
            editStage.setTitle("Edit Time Entry");
            editStage.setScene(new javafx.scene.Scene(editView, 600, 400));
            editStage.show();
        }
    }

    private void handleDelete() {
        TimeEntry selectedEntry = timeEntryTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null && !selectedEntry.isLocked()) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setHeaderText("Delete Time Entry");
            confirmDialog.setContentText("Are you sure you want to delete this time entry?");
            
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    // TODO: Implement delete in TimeEntryDAO
                    loadTimeEntries();
                    showSuccess("Time entry deleted successfully!");
                }
            });
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