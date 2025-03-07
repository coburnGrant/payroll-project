package grant.coburn.view.employee;

import grant.coburn.model.Employee;
import grant.coburn.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmployeeDashboardView extends VBox {
    private final User user;
    private final Employee employee;
    private final Stage stage;
    private Runnable onLogout;

    public EmployeeDashboardView(User user, Employee employee, Stage stage) {
        this.user = user;
        this.employee = employee;
        this.stage = stage;
        setupUI();
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text("Employee Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Use employee's full name if available, otherwise fall back to user ID
        String displayName = (employee != null) ? employee.getFullName() : user.getUserId();
        Label welcomeLabel = new Label("Welcome, " + displayName);
        welcomeLabel.setStyle("-fx-font-size: 16px;");

        // Add employee details if available
        VBox detailsBox = new VBox(10);
        detailsBox.setAlignment(Pos.CENTER);
        if (employee != null) {
            Text departmentText = new Text("Department: " + employee.getDepartment());
            Text jobTitleText = new Text("Position: " + employee.getJobTitle());
            Text emailText = new Text("Email: " + employee.getCompanyEmail());
            detailsBox.getChildren().addAll(departmentText, jobTitleText, emailText);
        }

        Button timeEntryButton = new Button("Enter Time");
        Button paycheckButton = new Button("View Paychecks");
        Button timeSheetButton = new Button("View Time Sheet");
        Button logoutButton = new Button("Logout");

        timeEntryButton.setOnAction(e -> handleTimeEntry());
        paycheckButton.setOnAction(e -> handlePaycheck());
        timeSheetButton.setOnAction(e -> handleTimeSheet());
        logoutButton.setOnAction(e -> handleLogout());

        // Make buttons wider
        timeEntryButton.setPrefWidth(200);
        paycheckButton.setPrefWidth(200);
        timeSheetButton.setPrefWidth(200);
        logoutButton.setPrefWidth(200);

        this.getChildren().addAll(
            title,
            welcomeLabel,
            detailsBox,
            timeEntryButton,
            timeSheetButton,
            paycheckButton,
            logoutButton
        );
    }

    private void handleTimeEntry() {
        if (employee != null) {
            TimeEntryView timeEntryView = new TimeEntryView(employee);
            timeEntryView.setOnBack(() -> {
                stage.setTitle("Payroll System - Employee Dashboard");
                stage.getScene().setRoot(this);
            });
            
            stage.setTitle("Payroll System - Time Entry");
            stage.getScene().setRoot(timeEntryView);
        } else {
            showError("Employee data not found");
        }
    }

    private void handlePaycheck() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("My Paychecks");
        
        EmployeePaychecksView paychecksView = new EmployeePaychecksView(employee);
        Scene scene = new Scene(paychecksView);
        stage.setScene(scene);
        stage.show();
    }

    private void handleTimeSheet() {
        if (employee != null) {
            TimeSheetView timeSheetView = new TimeSheetView(employee, stage);
            timeSheetView.setOnBack(() -> {
                stage.setTitle("Payroll System - Employee Dashboard");
                stage.getScene().setRoot(this);
            });
            
            stage.setTitle("Payroll System - Time Sheet");
            stage.getScene().setRoot(timeSheetView);
        } else {
            showError("Employee data not found");
        }
    }

    private void handleLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 