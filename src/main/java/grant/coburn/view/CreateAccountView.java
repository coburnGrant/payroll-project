package grant.coburn.view;

import java.util.function.Function;

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

public class CreateAccountView extends VBox {
    private TextField userIdField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private RadioButton adminRadio;
    private RadioButton employeeRadio;
    private Function<User, Boolean> onCreateAccount;
    private Runnable onBackToLogin;

    public CreateAccountView() {
        setupUI();
    }

    public void setOnCreateAccount(Function<User, Boolean> onCreateAccount) {
        this.onCreateAccount = onCreateAccount;
    }

    public void setOnBackToLogin(Runnable onBackToLogin) {
        this.onBackToLogin = onBackToLogin;
    }

    private void setupUI() {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text("Create Account");
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
        employeeRadio.setSelected(true);
        
        HBox radioBox = new HBox(10);
        radioBox.getChildren().addAll(adminRadio, employeeRadio);

        // Form Fields
        Label userIdLabel = new Label("User ID:");
        userIdField = new TextField();
        userIdField.setPromptText("Enter user ID");

        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Enter email");

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        Button createButton = new Button("Create Account");
        createButton.setOnAction(e -> handleCreateAccount());

        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> {
            if (onBackToLogin != null) {
                onBackToLogin.run();
            }
        });

        // Add components to grid
        int row = 0;
        grid.add(userTypeLabel, 0, row);
        grid.add(radioBox, 1, row++);
        grid.add(userIdLabel, 0, row);
        grid.add(userIdField, 1, row++);
        grid.add(emailLabel, 0, row);
        grid.add(emailField, 1, row++);
        grid.add(passwordLabel, 0, row);
        grid.add(passwordField, 1, row++);
        grid.add(confirmPasswordLabel, 0, row);
        grid.add(confirmPasswordField, 1, row++);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(createButton, backButton);
        grid.add(buttonBox, 1, row);

        this.getChildren().addAll(title, grid);
    }

    private void handleCreateAccount() {
        if (validateInput()) {
            User.UserType userType = adminRadio.isSelected() ? User.UserType.ADMIN : User.UserType.EMPLOYEE;
            User newUser = new User(
                userIdField.getText(),
                passwordField.getText(),
                userType,
                emailField.getText()
            );

            if (onCreateAccount != null && onCreateAccount.apply(newUser)) {
                showSuccess("Account created successfully!");
                clearFields();
            } else {
                showError("Failed to create account. Please try again.");
            }
        }
    }

    private boolean validateInput() {
        if (userIdField.getText().isEmpty()) {
            showError("Please enter a user ID");
            return false;
        }
        if (emailField.getText().isEmpty()) {
            showError("Please enter an email");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            showError("Please enter a password");
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void clearFields() {
        userIdField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        employeeRadio.setSelected(true);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 