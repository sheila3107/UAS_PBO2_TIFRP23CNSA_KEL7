package mycompany.sikasir;

import java.sql.SQLException;
import java.sql.*;

public class UserOperations {
    private Connection connection;

    public UserOperations() throws SQLException {
        connection = DatabaseConnection.getConnection();
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getConnection(); // Reconnect if the connection is closed
        }
    }

    // Login
    public boolean loginUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Pastikan untuk meng-hash password
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true; // Login sukses
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Login gagal
    }

    // Ambil profil pengguna berdasarkan username
    public User getProfile(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Jika tidak ditemukan
    }
    
    public void updateUser(String username, String newUsername, String newPassword) throws SQLException {
        String query = "UPDATE users SET username = ?, password = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newUsername);
            stmt.setString(2, newPassword); // Password harus di-hash di aplikasi nyata
            stmt.setString(3, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("Failed to update user.");
            }
        }
    }
}

 