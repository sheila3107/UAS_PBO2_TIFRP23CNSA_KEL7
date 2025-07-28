package mycompany.sikasir;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ManageUsersView {
    private Stage primaryStage;
    private Stage modalStage;
    private UserOperations userOperations;

    // Constructor
    public ManageUsersView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            userOperations = new UserOperations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method getView() mengembalikan Scene
    public Scene getView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Manage Users");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label userLabel = new Label("Select User to Edit:");
        ComboBox<String> userComboBox = new ComboBox<>();
        userComboBox.getItems().addAll("admin", "pegawai"); // Daftar pengguna yang bisa diedit
        userComboBox.setValue("pegawai"); // Default pilihan pegawai

        TextField usernameField = new TextField();
        usernameField.setPromptText("New Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password");

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            String selectedUser = userComboBox.getValue();
            String newUsername = usernameField.getText();
            String newPassword = passwordField.getText();

            if ("admin".equals(selectedUser) || "pegawai".equals(selectedUser)) {
                try {
                    userOperations.updateUser(selectedUser, newUsername, newPassword);
                    showSuccess("User updated successfully!");
                } catch (SQLException ex) {
                    showError("Error updating user.");
                    ex.printStackTrace();
                }
            }
        });

        layout.getChildren().addAll(titleLabel, userLabel, userComboBox, usernameField, passwordField, saveButton);

        // Mengembalikan Scene
        return new Scene(layout, 400, 300); // Pastikan tipe pengembalian adalah Scene
    }

    // Method untuk menampilkan pesan sukses
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method untuk menampilkan pesan error
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}