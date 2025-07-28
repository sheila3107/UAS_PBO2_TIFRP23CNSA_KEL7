package mycompany.sikasir;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = new BorderPane();
            LoginView loginView = new LoginView(primaryStage);
            root.setCenter(loginView.getView());

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("SiKasir");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}