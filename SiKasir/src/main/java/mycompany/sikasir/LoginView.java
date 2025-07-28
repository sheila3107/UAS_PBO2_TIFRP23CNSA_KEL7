package mycompany.sikasir;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;


public class LoginView {
    private Stage primaryStage;
    private UserOperations userOperations;

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public VBox getView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // Elemen Login
        Label titleLabel = new Label("Login");
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            try {
                userOperations = new UserOperations();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (userOperations.loginUser(username, password)) {
                DashboardView dashboardView = new DashboardView(primaryStage, username);
                primaryStage.setScene(new Scene(dashboardView.getView(), 800, 600));
            } else {
                showError("Login failed! Check username and password.");
            }
        });

        root.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);
        return root;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
