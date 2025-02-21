package grant.coburn;

import java.io.IOException;

import grant.coburn.dao.UserDAO;
import grant.coburn.model.User;
import grant.coburn.model.Employee;
import grant.coburn.util.PasswordUtil;
import grant.coburn.view.AdminDashboardView;
import grant.coburn.dao.EmployeeDAO;
import grant.coburn.view.*;
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

        PasswordUtil.print();
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
            showDashboard(user);
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
                employeeData = EmployeeDAO.shared.getEmployee(user.getEmployeeId());
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
        EmployeeDashboardView dashboard = new EmployeeDashboardView(user, employeeData);
        dashboard.setOnLogout(this::showLoginView);
        Scene dashboardScene = new Scene(dashboard, 600, 400);
        primaryStage.setTitle("Payroll System - Employee Dashboard");
        primaryStage.setScene(dashboardScene);
    }

    public static void main(String[] args) {
        launch();
    }
}