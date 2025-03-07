package grant.coburn.view.employee;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import grant.coburn.dao.TimeEntryDAO;
import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TimeSheetView extends BorderPane {
    private final Employee employee;
    private final Stage stage;
    private TableView<TimeEntry> timeEntryTable;
    private Button editButton;
    private Button deleteButton;
    private Button backButton;
    private Button addTimeEntryButton;
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
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: -fx-grey-100;");

        // Title section
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("Time Sheet");
        title.getStyleClass().add("title");
        Text subtitle = new Text(employee.getFullName());
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);
        this.setTop(titleBox);

        // Main content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("card");

        // Time entries table
        setupTimeEntryTable();
        content.getChildren().add(timeEntryTable);
        this.setCenter(content);

        // Button bar
        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(20, 0, 0, 0));
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        editButton = new Button("Edit Entry");
        deleteButton = new Button("Delete Entry");
        backButton = new Button("Back to Dashboard");
        addTimeEntryButton = new Button("Add Entry");

        editButton.setDisable(true);
        deleteButton.setDisable(true);
        
        editButton.getStyleClass().add("button-primary");
        deleteButton.getStyleClass().addAll("button-secondary", "button-danger");
        backButton.getStyleClass().add("button-secondary");
        addTimeEntryButton.getStyleClass().add("button-primary");
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());
        addTimeEntryButton.setOnAction(e -> handleAddTimeEntry());

        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        buttonBar.getChildren().addAll(addTimeEntryButton, editButton, deleteButton, backButton);
        this.setBottom(buttonBar);

        // Set up table selection listener
        timeEntryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            boolean isLocked = hasSelection && newSelection.isLocked();
            editButton.setDisable(!hasSelection || isLocked);
            deleteButton.setDisable(!hasSelection || isLocked);
        });
    }

    private void setupTimeEntryTable() {
        timeEntryTable = new TableView<>();
        timeEntryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Date column
        TableColumn<TimeEntry, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("workDate"));
        dateColumn.setMinWidth(150);
        dateColumn.setCellFactory(column -> new TableCell<TimeEntry, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                }
            }
        });

        // Hours column
        TableColumn<TimeEntry, Double> hoursColumn = new TableColumn<>("Hours");
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hoursWorked"));
        hoursColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        hoursColumn.setMinWidth(100);
        hoursColumn.setCellFactory(column -> new TableCell<TimeEntry, Double>() {
            @Override
            protected void updateItem(Double hours, boolean empty) {
                super.updateItem(hours, empty);
                if (empty || hours == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", hours));
                }
            }
        });

        // PTO column
        TableColumn<TimeEntry, Boolean> ptoColumn = new TableColumn<>("PTO");
        ptoColumn.setCellValueFactory(new PropertyValueFactory<>("pto"));
        ptoColumn.setStyle("-fx-alignment: CENTER;");
        ptoColumn.setMinWidth(80);
        ptoColumn.setCellFactory(column -> new TableCell<TimeEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean isPto, boolean empty) {
                super.updateItem(isPto, empty);
                if (empty || isPto == null) {
                    setText(null);
                } else {
                    setText(isPto ? "Yes" : "No");
                }
            }
        });

        // Locked column
        TableColumn<TimeEntry, Boolean> lockedColumn = new TableColumn<>("Locked");
        lockedColumn.setCellValueFactory(new PropertyValueFactory<>("locked"));
        lockedColumn.setStyle("-fx-alignment: CENTER;");
        lockedColumn.setMinWidth(80);
        lockedColumn.setCellFactory(column -> new TableCell<TimeEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean isLocked, boolean empty) {
                super.updateItem(isLocked, empty);
                if (empty || isLocked == null) {
                    setText(null);
                } else {
                    setText(isLocked ? "Yes" : "No");
                }
            }
        });

        timeEntryTable.getColumns().addAll(dateColumn, hoursColumn, ptoColumn, lockedColumn);
        timeEntryTable.setMinHeight(400);

        loadTimeEntries();
    }

    private void loadTimeEntries() {
        timeEntryTable.getItems().clear();
        timeEntryTable.getItems().addAll(TimeEntryDAO.shared.getTimeEntriesByEmployeeId(employee.getEmployeeId()));
    }

    private void handleAddTimeEntry() {
        Stage addStage = new Stage();
        addStage.initOwner(stage);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.setTitle("Add Time Entry");

        TimeEntryView addView = new TimeEntryView(employee);
        addView.setOnBack(() -> {
            loadTimeEntries(); // Refresh the table after adding
            addStage.close();
        });

        addView.setOnSave(() -> {
            loadTimeEntries(); // Refresh the table after adding
            addStage.close();
        });

        Scene scene = new Scene(addView);
        scene.getStylesheets().addAll(stage.getScene().getStylesheets());
        addStage.setScene(scene);
        addStage.showAndWait();
    }

    private void handleEdit() {
        TimeEntry selectedEntry = timeEntryTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null && !selectedEntry.isLocked()) {
            TimeEntryEditView editView = new TimeEntryEditView(employee, selectedEntry);
            editView.setOnSave(() -> {
                loadTimeEntries();
                showSuccess("Time entry updated successfully!");
            });
            editView.setOnCancel(() -> {});
            
            Stage editStage = new Stage();
            editStage.initOwner(stage);
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.setTitle("Edit Time Entry");
            Scene scene = new Scene(editView);
            scene.getStylesheets().addAll(stage.getScene().getStylesheets());
            editStage.setScene(scene);
            editStage.showAndWait();
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
                if (response == ButtonType.OK) {
                    boolean success = TimeEntryDAO.shared.deleteTimeEntry(selectedEntry);
                    if (success) {
                        loadTimeEntries();
                        showSuccess("Time entry deleted successfully!");
                    } else {
                        showError("Failed to delete time entry. It may be locked or no longer exists.");
                    }
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 