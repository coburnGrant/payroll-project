package grant.coburn.view;

import grant.coburn.model.Employee;
import grant.coburn.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EmployeeDashboardView extends VBox {
    private final User user;
    private final Employee employeeData;
    private Runnable onLogout;

    public EmployeeDashboardView(User user, Employee employeeData) {
        this.user = user;
        this.employeeData = employeeData;
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
        String displayName = (employeeData != null) ? employeeData.getFullName() : user.getUserId();
        Label welcomeLabel = new Label("Welcome, " + displayName);
        welcomeLabel.setStyle("-fx-font-size: 16px;");

        // Add employee details if available
        VBox detailsBox = new VBox(10);
        detailsBox.setAlignment(Pos.CENTER);
        if (employeeData != null) {
            Text departmentText = new Text("Department: " + employeeData.getDepartment());
            Text jobTitleText = new Text("Position: " + employeeData.getJobTitle());
            Text emailText = new Text("Email: " + employeeData.getCompanyEmail());
            detailsBox.getChildren().addAll(departmentText, jobTitleText, emailText);
        }

        Button timeEntryButton = new Button("Enter Time");
        Button ptoButton = new Button("Request PTO");
        Button paycheckButton = new Button("View Paychecks");
        Button logoutButton = new Button("Logout");

        timeEntryButton.setOnAction(e -> handleTimeEntry());
        ptoButton.setOnAction(e -> handlePTO());
        paycheckButton.setOnAction(e -> handlePaycheck());
        logoutButton.setOnAction(e -> handleLogout());

        // Make buttons wider
        timeEntryButton.setPrefWidth(200);
        ptoButton.setPrefWidth(200);
        paycheckButton.setPrefWidth(200);
        logoutButton.setPrefWidth(200);

        this.getChildren().addAll(
            title,
            welcomeLabel,
            detailsBox,
            timeEntryButton,
            ptoButton,
            paycheckButton,
            logoutButton
        );
    }

    private void handleTimeEntry() {
        // TODO: Implement time entry
        System.out.println("Time entry clicked");
    }

    private void handlePTO() {
        // TODO: Implement PTO request
        System.out.println("PTO request clicked");
    }

    private void handlePaycheck() {
        // TODO: Implement paycheck view
        System.out.println("View paychecks clicked");
    }

    private void handleLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }
} 