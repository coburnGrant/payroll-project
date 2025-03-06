package grant.coburn.view;

import java.time.format.DateTimeFormatter;
import java.util.List;

import grant.coburn.dao.TimeEntryDAO;
import grant.coburn.model.Employee;
import grant.coburn.model.TimeEntry;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TimeSheetView {
    private final Stage stage;
    private final Employee employee;
    private final TimeEntryDAO timeEntryDAO;
    private final TableView<TimeEntry> timeEntryTable;
    private final Runnable onBack;

    public TimeSheetView(Stage stage, Employee employee, Runnable onBack) {
        this.stage = stage;
        this.employee = employee;
        this.timeEntryDAO = TimeEntryDAO.shared;
        this.onBack = onBack;
        this.timeEntryTable = createTimeEntryTable();
        setupUI();
    }

    private void setupUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Title
        Label titleLabel = new Label(employee.getFirstName() + " " + employee.getLastName() + "'s Time Sheet");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.setTop(titleLabel);

        // Time entries table
        root.setCenter(timeEntryTable);

        // Buttons
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> {
            stage.hide();
            if (onBack != null) {
                onBack.run();
            }
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().add(backButton);
        root.setBottom(buttonBox);

        // Scene setup
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Employee Time Sheet");
    }

    private TableView<TimeEntry> createTimeEntryTable() {
        TableView<TimeEntry> table = new TableView<>();
        
        TableColumn<TimeEntry, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWorkDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
            )
        );

        TableColumn<TimeEntry, String> hoursColumn = new TableColumn<>("Hours Worked");
        hoursColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f", cellData.getValue().getHoursWorked())
            )
        );

        TableColumn<TimeEntry, String> regularHoursColumn = new TableColumn<>("Regular Hours");
        regularHoursColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f", cellData.getValue().getRegularHours())
            )
        );

        TableColumn<TimeEntry, String> overtimeHoursColumn = new TableColumn<>("Overtime Hours");
        overtimeHoursColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f", cellData.getValue().getOvertimeHours())
            )
        );

        TableColumn<TimeEntry, String> ptoColumn = new TableColumn<>("PTO");
        ptoColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isPto() ? "Yes" : "No"
            )
        );

        TableColumn<TimeEntry, String> lockedColumn = new TableColumn<>("Locked");
        lockedColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isLocked() ? "Yes" : "No"
            )
        );

        table.getColumns().addAll(dateColumn, hoursColumn, regularHoursColumn, overtimeHoursColumn, ptoColumn, lockedColumn);
        
        // Load time entries
        List<TimeEntry> timeEntries = timeEntryDAO.getTimeEntriesByEmployeeId(employee.getEmployeeId());
        table.getItems().addAll(timeEntries);

        return table;
    }

    public void show() {
        stage.show();
    }
} 