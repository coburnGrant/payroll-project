package grant.coburn.view;

import grant.coburn.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AdminDashboardView extends VBox {
    private final User user;
    private Runnable onLogout;

    public AdminDashboardView(User user) {
        this.user = user;
        setupUI();
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text("Admin Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label welcomeLabel = new Label("Welcome, " + user.getUserId());
        welcomeLabel.setStyle("-fx-font-size: 16px;");

        Button employeeMgmtButton = new Button("Manage Employees");
        Button payrollButton = new Button("Process Payroll");
        Button reportsButton = new Button("Generate Reports");
        Button logoutButton = new Button("Logout");

        employeeMgmtButton.setOnAction(e -> handleEmployeeManagement());
        payrollButton.setOnAction(e -> handlePayroll());
        reportsButton.setOnAction(e -> handleReports());
        logoutButton.setOnAction(e -> handleLogout());

        // Make buttons wider
        employeeMgmtButton.setPrefWidth(200);
        payrollButton.setPrefWidth(200);
        reportsButton.setPrefWidth(200);
        logoutButton.setPrefWidth(200);

        this.getChildren().addAll(
            title,
            welcomeLabel,
            employeeMgmtButton,
            payrollButton,
            reportsButton,
            logoutButton
        );
    }

    private void handleEmployeeManagement() {
        // TODO: Implement employee management
        System.out.println("Employee management clicked");
    }

    private void handlePayroll() {
        // TODO: Implement payroll processing
        System.out.println("Payroll processing clicked");
    }

    private void handleReports() {
        // TODO: Implement reports generation
        System.out.println("Reports clicked");
    }

    private void handleLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }
} 