package grant.coburn.view.admin;

import grant.coburn.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminDashboardView extends VBox {
    private final User user;
    private final Stage stage;
    private Runnable onLogout;

    public AdminDashboardView(User user, Stage stage) {
        this.user = user;
        this.stage = stage;
        setupUI();
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    private void setupUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(40));
        this.setSpacing(40);
        this.setStyle("-fx-background-color: -fx-grey-100;");

        // Header section with welcome message and logout
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox welcomeBox = new VBox(5);
        Text title = new Text("Admin Dashboard");
        title.getStyleClass().add("title");
        Label welcomeLabel = new Label("Welcome back, " + user.getUserId());
        welcomeLabel.getStyleClass().add("subtitle");
        welcomeBox.getChildren().addAll(title, welcomeLabel);
        
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("button-secondary", "button-danger");
        logoutButton.setOnAction(e -> handleLogout());
        
        HBox.setHgrow(welcomeBox, Priority.ALWAYS);
        header.getChildren().addAll(welcomeBox, logoutButton);

        // Main content with action cards
        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.CENTER);

        // Employee Management Card
        VBox employeeMgmtCard = createActionCard(
            "Manage Employees",
            "Add, edit, or remove employee records",
            "button-primary",
            e -> showEmployeeManagement()
        );

        // Payroll Processing Card
        VBox payrollCard = createActionCard(
            "Process Payroll",
            "Calculate and process employee payroll",
            "button-primary",
            e -> handlePayroll()
        );

        // Reports Card
        VBox reportsCard = createActionCard(
            "Generate Reports",
            "Create and export payroll reports",
            "button-primary",
            e -> handleReports()
        );

        mainContent.getChildren().addAll(employeeMgmtCard, payrollCard, reportsCard);

        this.getChildren().addAll(header, mainContent);
    }

    private VBox createActionCard(String title, String description, String buttonStyle, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(250);
        card.setMinHeight(200);

        Text titleText = new Text(title);
        titleText.getStyleClass().add("subtitle");
        titleText.setStyle("-fx-font-weight: bold;");

        Text descText = new Text(description);
        descText.setStyle("-fx-text-alignment: center; -fx-fill: -fx-grey-700;");
        descText.setWrappingWidth(200);

        Button actionButton = new Button(title);
        actionButton.getStyleClass().add(buttonStyle);
        actionButton.setMaxWidth(Double.MAX_VALUE);
        actionButton.setOnAction(action);

        card.getChildren().addAll(titleText, descText, actionButton);
        return card;
    }

    private void showEmployeeManagement() {
        // Store the current dashboard scene
        Scene dashboardScene = stage.getScene();
        
        EmployeeManagementView employeeManagement = new EmployeeManagementView(stage);
        employeeManagement.setOnBack(() -> {
            stage.setScene(dashboardScene);
        });
        
        Scene scene = new Scene(employeeManagement, 800, 600);
        scene.getStylesheets().addAll(dashboardScene.getStylesheets());
        stage.setScene(scene);
    }

    private void handlePayroll() {
        // Store the current dashboard scene
        Scene dashboardScene = stage.getScene();
        
        PayrollProcessingView payrollView = new PayrollProcessingView(stage);
        payrollView.setOnBack(() -> {
            stage.setScene(dashboardScene);
        });
        Scene scene = new Scene(payrollView, 800, 600);
        scene.getStylesheets().addAll(dashboardScene.getStylesheets());
        stage.setScene(scene);
    }

    private void handleReports() {
        GenerateReportView reportView = new GenerateReportView();
        Scene scene = new Scene(reportView, 600, 400);
        scene.getStylesheets().addAll(stage.getScene().getStylesheets());
        Stage reportStage = new Stage();
        reportStage.initModality(Modality.APPLICATION_MODAL);
        reportStage.initOwner(stage);
        reportStage.setTitle("Generate Reports");
        reportStage.setScene(scene);
        reportStage.showAndWait();
    }

    private void handleLogout() {
        if (onLogout != null) {
            onLogout.run();
        }
    }
} 