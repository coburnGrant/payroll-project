package grant.coburn;

import java.io.IOException;

import grant.coburn.dao.EmployeeDAO;
import grant.coburn.dao.UserDAO;
import grant.coburn.model.Employee;
import grant.coburn.model.User;
import grant.coburn.view.AdminDashboardView;
import grant.coburn.view.ChangePasswordView;
import grant.coburn.view.CreateAccountView;
import grant.coburn.view.EmployeeDashboardView;
import grant.coburn.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    private static Scene scene;
    private Stage primaryStage;
    private BorderPane rootLayout;

    private UserDAO userDAO;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.userDAO = UserDAO.shared;

        showLoginView();
    }

    private void showLoginView() {
        LoginView loginView = new LoginView();
        loginView.setOnLogin(this::handleLogin);
        loginView.setOnCreateAccountClick(this::showCreateAccountView);

        Scene loginScene = new Scene(loginView, 400, 300);
        primaryStage.setTitle("Payroll System - Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private User handleLogin(String userId, String password) {
        User user = userDAO.authenticateUser(userId, password);
        if (user != null) {
            if (userDAO.mustChangePassword(user.getUserId())) {
                showChangePasswordView(user, true);
            } else {
                showDashboard(user);
            }
            System.out.println("Login successful for user: " + user.getUserId());
        } else {
            System.out.println("Login failed for user: " + userId);
        }
        return user;
    }

    private void showCreateAccountView() {
        CreateAccountView createAccountView = new CreateAccountView();
        createAccountView.setOnCreateAccount(this::handleCreateAccount);
        createAccountView.setOnBackToLogin(this::showLoginView);
        Scene createAccountScene = new Scene(createAccountView, 400, 400);
        primaryStage.setTitle("Payroll System - Create Account");
        primaryStage.setScene(createAccountScene);
    }

    private Boolean handleCreateAccount(User user) {
        return userDAO.createUser(user);
    }

    private void showDashboard(User user) {
        if (user.getUserType() == User.UserType.ADMIN) {
            showAdminDashboard(user);
        } else {
            // For employees, load their employee data
            Employee employeeData = null;
            if (user.getEmployeeId() != null) {
                System.out.println("Found employee ID: " + user.getEmployeeId());
                employeeData = EmployeeDAO.shared.getEmployee(user.getEmployeeId());
                if (employeeData != null) {
                    System.out.println("Found employee: " + employeeData.getFullName());
                } else {
                    System.out.println("Could not find employee with ID: " + user.getEmployeeId());
                }
            } else {
                System.out.println("No employee ID found for user: " + user.getUserId());
            }
            showEmployeeDashboard(user, employeeData);
        }
    }

    private void showAdminDashboard(User user) {
        AdminDashboardView dashboard = new AdminDashboardView(user, primaryStage);
        dashboard.setOnLogout(this::showLoginView);
        Scene dashboardScene = new Scene(dashboard, 600, 400);
        primaryStage.setTitle("Payroll System - Admin Dashboard");
        primaryStage.setScene(dashboardScene);
    }

    private void showEmployeeDashboard(User user, Employee employeeData) {
        EmployeeDashboardView dashboard = new EmployeeDashboardView(user, employeeData, primaryStage);
        dashboard.setOnLogout(this::showLoginView);
        Scene dashboardScene = new Scene(dashboard, 600, 400);
        primaryStage.setTitle("Payroll System - Employee Dashboard");
        primaryStage.setScene(dashboardScene);
    }

    private void showChangePasswordView(User user, boolean isForced) {
        ChangePasswordView changePasswordView = new ChangePasswordView(user, isForced);
        changePasswordView.setOnSuccess(() -> showDashboard(user));
        if (!isForced) {
            changePasswordView.setOnBack(() -> showDashboard(user));
        }
        Scene changePasswordScene = new Scene(changePasswordView, 400, 300);
        primaryStage.setTitle("Payroll System - Change Password");
        primaryStage.setScene(changePasswordScene);
    }

    public static void main(String[] args) {
        launch();
    }
}