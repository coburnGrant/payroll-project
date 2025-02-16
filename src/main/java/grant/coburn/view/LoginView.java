package grant.coburn.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginView extends BorderPane {
    private VBox mainBox;

    private HBox userIDHBox;
    private HBox passwordHBox;

    private HBox userTypeSelectionVBox;

    private Button loginButton;

    public LoginView() {
        initInterface();
        displayMainBox();
    }

    private void initInterface() {
        setPadding(new Insets(20));

        initUserIDTextField();
        initPasswordTextField();
        
        initUserTypeSelectionVBox();
        initMainBox();
    }
    
    private void initUserTypeSelectionVBox() {
        userTypeSelectionVBox = new HBox(10);
        userTypeSelectionVBox.setAlignment(Pos.CENTER);
    
        Text title = new Text("Login as:");
    
        // Create radio buttons
        RadioButton adminButton = new RadioButton("Admin");
        RadioButton employeeButton = new RadioButton("Employee");
    
        // Create a ToggleGroup to allow only one selection at a time
        ToggleGroup userTypeGroup = new ToggleGroup();
        adminButton.setToggleGroup(userTypeGroup);
        employeeButton.setToggleGroup(userTypeGroup);
    
        userTypeSelectionVBox.getChildren().addAll(title, adminButton, employeeButton);
    }

    private void initUserIDTextField() {
        userIDHBox = new HBox(5);
        Text userIDLabel = new Text("User ID");
        TextField userIDTextField = new TextField();
        userIDHBox.getChildren().addAll(userIDLabel, userIDTextField);
        userIDHBox.setAlignment(Pos.CENTER);
    }

    private void initPasswordTextField() {
        passwordHBox = new HBox(5);
        Text passwordFieldLabel = new Text("Password");
        PasswordField loginPasswordField = new PasswordField();
        passwordHBox.getChildren().addAll(passwordFieldLabel, loginPasswordField);
        passwordHBox.setAlignment(Pos.CENTER);
    }

    private void initMainBox() {
        mainBox = new VBox(20);

        Text loginText = new Text("Login to Payroll");

        loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            System.err.println("Login Clicked!");
        });

        mainBox.getChildren().addAll(loginText, userIDHBox, passwordHBox, userTypeSelectionVBox, loginButton);
        mainBox.setAlignment(Pos.CENTER);
    }

    private void displayMainBox() {
        setCenter(mainBox);
    }
}
