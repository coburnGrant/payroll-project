package grant.coburn.view;

import java.util.function.BiFunction;

import grant.coburn.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
        this.setPadding(new Insets(40));
        this.setSpacing(30);
        this.getStyleClass().add("card");

        // Title section
        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);
        Text title = new Text("Welcome Back");
        title.getStyleClass().add("title");
        Text subtitle = new Text("Sign in to access your account");
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        // Form section
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(300);

        // User Type Radio Buttons
        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        ToggleGroup userTypeGroup = new ToggleGroup();
        
        adminRadio = new RadioButton("Admin");
        adminRadio.setToggleGroup(userTypeGroup);
        
        employeeRadio = new RadioButton("Employee");
        employeeRadio.setToggleGroup(userTypeGroup);
        employeeRadio.setSelected(true);
        
        radioBox.getChildren().addAll(adminRadio, employeeRadio);

        // Input fields
        userIdField = new TextField();
        userIdField.setPromptText("Enter your user ID");
        userIdField.getStyleClass().add("text-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("text-field");
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER && 
                !userIdField.getText().isEmpty() && 
                !passwordField.getText().isEmpty()) {
                handleLogin();
            }
        });

        // Buttons
        Button loginButton = new Button("Sign In");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());

        Button createAccountButton = new Button("Create Account");
        createAccountButton.getStyleClass().add("button-secondary");
        createAccountButton.setMaxWidth(Double.MAX_VALUE);
        createAccountButton.setOnAction(e -> {
            if (onCreateAccountClick != null) {
                onCreateAccountClick.run();
            }
        });

        // Add all components to the form
        formBox.getChildren().addAll(
            radioBox,
            userIdField,
            passwordField,
            loginButton,
            createAccountButton
        );

        // Add everything to the main container
        this.getChildren().addAll(titleBox, formBox);
    }

    private void handleLogin() {
        if (onLogin != null) {
            String userId = userIdField.getText();
            String password = passwordField.getText();

            if (userId.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields");
                return;
            }

            User user = onLogin.apply(userId, password);
            if (user == null) {
                showError("Invalid credentials");
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
