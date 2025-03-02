package grant.coburn.view;

import java.util.function.BiFunction;

import grant.coburn.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginView extends VBox {
    private TextField userIdField;
    private PasswordField passwordField;
    private RadioButton adminRadio;
    private RadioButton employeeRadio;
    private BiFunction<String, String, User> onLogin;
    private Runnable onCreateAccountClick;

    public LoginView() {
        setupUI();
    }

    public void setOnLogin(BiFunction<String, String, User> onLogin) {
        this.onLogin = onLogin;
    }

    public void setOnCreateAccountClick(Runnable onCreateAccountClick) {
        this.onCreateAccountClick = onCreateAccountClick;
    }

    private void setupUI() {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text("Payroll System Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // User Type Radio Buttons
        Label userTypeLabel = new Label("User Type:");
        ToggleGroup userTypeGroup = new ToggleGroup();
        
        adminRadio = new RadioButton("Admin");
        adminRadio.setToggleGroup(userTypeGroup);
        
        employeeRadio = new RadioButton("Employee");
        employeeRadio.setToggleGroup(userTypeGroup);
        employeeRadio.setSelected(true); // Default selection
        
        HBox radioBox = new HBox(10);
        radioBox.getChildren().addAll(adminRadio, employeeRadio);

        Label userIdLabel = new Label("User ID:");
        userIdField = new TextField();
        userIdField.setPromptText("Enter your user ID");

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER && 
                !userIdField.getText().isEmpty() && 
                !passwordField.getText().isEmpty()) {
                handleLogin();
            }
        });

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin());

        // Add components to grid
        grid.add(userTypeLabel, 0, 0);
        grid.add(radioBox, 1, 0);
        grid.add(userIdLabel, 0, 1);
        grid.add(userIdField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(loginButton, 1, 3);

        this.getChildren().addAll(title, grid, loginButton);
    }

    private void handleLogin() {
        if (onLogin != null) {
            String userId = userIdField.getText();
            String password = passwordField.getText();

            if (userId.isEmpty() || password.isEmpty()) {
                showError("Please enter both user ID and password");
                return;
            }

            // Verify user type matches selection
            User user = onLogin.apply(userId, password);
            if (user == null) {
                showError("Invalid credentials");
            } else {
                boolean isAdmin = user.getUserType() == User.UserType.ADMIN;

                if ((isAdmin && !adminRadio.isSelected()) || (!isAdmin && !employeeRadio.isSelected())) {
                    showError("Selected user type does not match account type");
                }
                
                // Login successful - handled by callback
            }
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
