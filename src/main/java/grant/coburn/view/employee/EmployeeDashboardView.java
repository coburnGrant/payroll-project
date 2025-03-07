package grant.coburn.view.employee;

import grant.coburn.model.Employee;
import grant.coburn.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        this.setPadding(new Insets(40));
        this.setSpacing(40);
        this.setStyle("-fx-background-color: -fx-grey-100;");

        // Header section with welcome message and logout
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox welcomeBox = new VBox(5);
        Text title = new Text("Employee Dashboard");
        title.getStyleClass().add("title");
        
        // Use employee's full name if available, otherwise fall back to user ID
        String displayName = (employee != null) ? employee.getFullName() : user.getUserId();
        Label welcomeLabel = new Label("Welcome back, " + displayName);
        welcomeLabel.getStyleClass().add("subtitle");
        welcomeBox.getChildren().addAll(title, welcomeLabel);
        
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("button-secondary", "button-danger");
        logoutButton.setOnAction(e -> handleLogout());
        
        HBox.setHgrow(welcomeBox, Priority.ALWAYS);
        header.getChildren().addAll(welcomeBox, logoutButton);

        // Employee details card
        VBox detailsCard = new VBox(10);
        detailsCard.getStyleClass().add("card");
        detailsCard.setPadding(new Insets(20));
        detailsCard.setMaxWidth(800);
        
        if (employee != null) {
            Text detailsTitle = new Text("Employee Information");
            detailsTitle.getStyleClass().add("subtitle");
            detailsTitle.setStyle("-fx-font-weight: bold;");
            
            HBox detailsGrid = new HBox(40);
            detailsGrid.setAlignment(Pos.CENTER);
            
            // Left column
            VBox leftColumn = new VBox(10);
            leftColumn.getChildren().addAll(
                createDetailLabel("Department:", employee.getDepartment()),
                createDetailLabel("Position:", employee.getJobTitle())
            );
            
            // Right column
            VBox rightColumn = new VBox(10);
            rightColumn.getChildren().addAll(
                createDetailLabel("Email:", employee.getCompanyEmail()),
                createDetailLabel("Status:", employee.getStatus().toString())
            );
            
            detailsGrid.getChildren().addAll(leftColumn, rightColumn);
            detailsCard.getChildren().addAll(detailsTitle, detailsGrid);
        }

        // Main content with action cards
        HBox mainContent = new HBox(20);
        mainContent.setAlignment(Pos.CENTER);

        // Time Entry Card
        VBox timeEntryCard = createActionCard(
            "Enter Time",
            "Record your work hours and PTO",
            "button-primary",
            e -> handleTimeEntry()
        );

        // Time Sheet Card
        VBox timeSheetCard = createActionCard(
            "View Time Sheet",
            "Review your time entries",
            "button-primary",
            e -> handleTimeSheet()
        );

        // Paychecks Card
        VBox paycheckCard = createActionCard(
            "View Paychecks",
            "Access your paycheck history",
            "button-primary",
            e -> handlePaycheck()
        );

        mainContent.getChildren().addAll(timeEntryCard, timeSheetCard, paycheckCard);

        this.getChildren().addAll(header, detailsCard, mainContent);
    }

    private HBox createDetailLabel(String label, String value) {
        HBox container = new HBox(10);
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-weight: bold;");
        Label valueText = new Label(value);
        container.getChildren().addAll(labelText, valueText);
        return container;
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

    private void handleTimeEntry() {
        if (employee == null) {
            showError("No employee data found");
            return;
        }

        Stage timeEntryStage = new Stage();
        TimeEntryView timeEntryView = new TimeEntryView(employee);
        timeEntryView.setOnBack(() -> {
            timeEntryStage.close();
        });
        Scene scene = new Scene(timeEntryView, 600, 800);
        scene.getStylesheets().addAll(stage.getScene().getStylesheets());
        timeEntryStage.initModality(Modality.APPLICATION_MODAL);
        timeEntryStage.initOwner(stage);
        timeEntryStage.setTitle("Time Entry");
        timeEntryStage.setScene(scene);
        timeEntryStage.showAndWait();
    }

    private void handlePaycheck() {
        if (employee == null) {
            showError("No employee data found");
            return;
        }

        EmployeePaychecksView paychecksView = new EmployeePaychecksView(employee);
        Scene scene = new Scene(paychecksView, 800, 600);
        scene.getStylesheets().addAll(stage.getScene().getStylesheets());
        Stage paycheckStage = new Stage();
        paycheckStage.initModality(Modality.APPLICATION_MODAL);
        paycheckStage.initOwner(stage);
        paycheckStage.setTitle("My Paychecks");
        paycheckStage.setScene(scene);
        paycheckStage.showAndWait();
    }

    private void handleTimeSheet() {
        if (employee == null) {
            showError("No employee data found");
            return;
        }

        Stage timeSheetStage = new Stage();
        TimeSheetView timeSheetView = new TimeSheetView(employee, stage);
        timeSheetView.setOnBack(() -> {
            timeSheetStage.close();
        });
        Scene scene = new Scene(timeSheetView, 800, 600);
        scene.getStylesheets().addAll(stage.getScene().getStylesheets());
        timeSheetStage.initModality(Modality.APPLICATION_MODAL);
        timeSheetStage.initOwner(stage);
        timeSheetStage.setTitle("Time Sheet");
        timeSheetStage.setScene(scene);
        timeSheetStage.showAndWait();
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