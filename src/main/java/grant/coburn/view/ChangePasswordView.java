package grant.coburn.view;

import grant.coburn.dao.UserDAO;
import grant.coburn.model.User;
import grant.coburn.util.PasswordValidationException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChangePasswordView extends VBox {
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private final User user;
    private final boolean isForced;
    private Runnable onSuccess;
    private Runnable onBack;

    public ChangePasswordView(User user, boolean isForced) {
        this.user = user;
        this.isForced = isForced;
        setupUI();
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    private void setupUI() {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Text title = new Text(isForced ? "Change Password Required" : "Change Password");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        addFormField(grid, "Current Password:", currentPasswordField, 0);

        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        addFormField(grid, "New Password:", newPasswordField, 1);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");
        addFormField(grid, "Confirm Password:", confirmPasswordField, 2);

        Button saveButton = new Button("Save Password");
        saveButton.setOnAction(e -> handleSave());

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> handleBack());
        backButton.setVisible(!isForced);  // Hide back button if password change is forced

        this.getChildren().addAll(title, grid, saveButton);
        if (!isForced) {
            this.getChildren().add(backButton);
        }
    }

    private void addFormField(GridPane grid, String label, Control field, int row) {
        grid.add(new Label(label), 0, row);
        grid.add(field, 1, row);
    }

    private void handleSave() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("New passwords do not match");
            return;
        }

        boolean success;
        String errorMessage = "Current password is incorrect";

        try {
            success = UserDAO.shared.changePassword(user.getUserId(), currentPassword, newPassword);
        } catch (PasswordValidationException e) {
            success = false;
            errorMessage = e.getMessage();
        }

        if (success) {
            showSuccess("Password changed successfully");
            
            if (onSuccess != null) {
                onSuccess.run();
            }
        } else {
            showError(errorMessage);
        }
    }

    private void handleBack() {
        if (onBack != null) {
            onBack.run();
        }
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