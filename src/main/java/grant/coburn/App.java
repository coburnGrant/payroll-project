package grant.coburn;

import java.io.IOException;

import grant.coburn.view.LoginView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class App extends Application {
    private static Scene scene;
    private Stage primaryStage;
    private BorderPane rootLayout;

    private Stage loginStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Payroll");

        this.rootLayout = new BorderPane();

        boolean isLoggedIn = false;

        if (isLoggedIn) {
            showPrimaryStage();
        } else {
            showLoginStage();
        }
    }

    private void showLoginStage() {
        LoginView loginView = new LoginView();

        Scene loginScene = new Scene(loginView);
        
        loginStage = new Stage();
        loginStage.setTitle("Login to Payroll");
        loginStage.setScene(loginScene);
        loginStage.show();
    }

    private void showPrimaryStage() {
        VBox helloWorld = new VBox();

        helloWorld.getChildren().add(new Text("Hello world!"));

        rootLayout.setCenter(helloWorld);
        
        scene = new Scene(rootLayout, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}