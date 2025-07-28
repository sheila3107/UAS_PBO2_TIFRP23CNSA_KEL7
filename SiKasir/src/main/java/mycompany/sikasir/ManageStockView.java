package mycompany.sikasir;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene; // Tetap perlu jika Anda ingin menampilkan Scene secara terpisah
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Pastikan ini ada
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.Node;

// Ini harus ada:
public class ManageStockView extends VBox {
    private Stage primaryStage;
    private String role; // Untuk menentukan apakah view hanya untuk melihat atau mengelola

    private TableView<StockItem> tableView; // Menggunakan StockItem sesuai kode Anda
    private ObservableList<StockItem> stockList;

    private Button addButton;
    private Button backButton;

    // Constructor
    public ManageStockView(Stage primaryStage, String role) {
        this.primaryStage = primaryStage;
        this.role = role; // Pastikan role disimpan
        stockList = FXCollections.observableArrayList();

        // Inisialisasi properti VBox (this)
        setPadding(new Insets(20));
        setSpacing(10);

        // Initialize the "Add Item" button
        addButton = new Button("Add Item");
        addButton.setOnAction(e -> {
            showStockModal(null); // Show form to add new stock item
        });

        // Initialize the back button (for admin)
        backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> {
            // Penting: Pastikan Anda mendapatkan username yang benar untuk kembali ke Dashboard
            // Untuk demo, asumsikan "admin" atau ambil dari suatu tempat yang menyimpan info login
            // Anda perlu pass username yang sebenarnya dari DashboardView ke ManageStockView
            // Misalnya: new DashboardView(primaryStage, this.usernameDariDashboard)
            String loggedInUsername = "admin"; // <--- Ganti dengan username yang sedang login
            DashboardView dashboardView = new DashboardView(primaryStage, loggedInUsername);
            primaryStage.setScene(new Scene(dashboardView.getView(), 800, 600));
        });

        // Initialize the TableView first
        tableView = new TableView<>();
        TableColumn<StockItem, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<StockItem, String> nameColumn = new TableColumn<>("Item Name");
        TableColumn<StockItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<StockItem, Double> priceColumn = new TableColumn<>("Price");
        TableColumn<StockItem, String> actionColumn = new TableColumn<>("Action");

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        actionColumn.setCellFactory(param -> {
            TableCell<StockItem, String> cell = new TableCell<StockItem, String>() {
                private final Button editButton = new Button("Edit");

                {
                    editButton.setOnAction(event -> {
                        StockItem stockItem = getTableView().getItems().get(getIndex());
                        showStockModal(stockItem); // Show modal to edit the stock item
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || "pegawai".equals(role)) { // Menggunakan parameter role dari konstruktor
                        setGraphic(null);  // Remove Edit button for pegawai
                    } else {
                        setGraphic(editButton);  // Show Edit button for admin
                    }
                }
            };
            return cell;
        });

        tableView.getColumns().addAll(idColumn, nameColumn, quantityColumn, priceColumn, actionColumn);
        tableView.setItems(stockList); // Bind stockList to the TableView

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            loadStockData(); // Reload stock data from the database after refresh
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(addButton, refreshButton);

        // Langsung tambahkan ke `this` (VBox)
        getChildren().addAll(backButton, new Label("Manage Stock"), tableView);

        // Tambahkan buttonBox hanya jika role adalah admin
        if ("admin".equals(role)) {
            getChildren().add(buttonBox); // Tambahkan buttonBox untuk admin
        }

        // Call the method to load stock data now that the components are initialized
        loadStockData();

        // Disable the edit and add buttons if the role is "pegawai"
        if ("pegawai".equals(role)) {
            disableEditAndAddButtons(); // Disable edit and add buttons for pegawai
        }

        // HAPUS BARIS INI (jika ada): Scene scene = new Scene(layout, 600, 400); primaryStage.setScene(scene);
    }

    // Metode untuk menonaktifkan tombol edit dan tambah untuk peran "pegawai"
    private void disableEditAndAddButtons() {
        addButton.setDisable(true); // Disable Add Item button for "pegawai"
        // Logika untuk menonaktifkan kolom 'Action' sudah ada di actionColumn.setCellFactory
        // Anda mungkin juga ingin mencegah pemilihan baris atau interaksi lain
        tableView.setSelectionModel(null); // Membuat tabel tidak bisa dipilih
    }

    // Method to load stock data from the database
    private void loadStockData() {
        String query = "SELECT id, name, quantity, price FROM stock"; // Ensure this is the correct table name and columns
        stockList.clear();  // Clear existing data in the list before reloading

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                stockList.add(new StockItem(id, name, quantity, price)); // Add data to the list
            }

            if (stockList.isEmpty()) {
                System.out.println("No data found in the stock table.");
            } else {
                System.out.println("Stock data loaded successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Error loading stock data: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading stock data: " + e.getMessage());
            alert.show();
        }
    }

    // Method to show the modal for adding or editing a stock item
    private void showStockModal(StockItem stockItem) {
        Stage modalStage = new Stage();
        modalStage.setTitle(stockItem == null ? "Add Item" : "Edit Item");

        TextField nameField = new TextField(stockItem == null ? "" : stockItem.getName());
        TextField quantityField = new TextField(stockItem == null ? "" : String.valueOf(stockItem.getQuantity()));
        TextField priceField = new TextField(stockItem == null ? "" : String.valueOf(stockItem.getPrice()));

        Button saveButton = new Button(stockItem == null ? "Add" : "Save");
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            int quantity;
            double price;

            try {
                quantity = Integer.parseInt(quantityField.getText());
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Quantity and Price must be numbers.");
                alert.show();
                return;
            }

            if (name.isEmpty() || quantity <= 0 || price <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "All fields must be filled with valid values.");
                alert.show();
                return;
            }

            if (stockItem == null) {
                addStockItem(name, quantity, price); // Add new item
            } else {
                updateStockItem(stockItem.getId(), name, quantity, price); // Update existing item
            }
            modalStage.close();
        });

        VBox modalLayout = new VBox(10);
        modalLayout.setPadding(new Insets(20));
        modalLayout.getChildren().addAll(new Label("Item Name:"), nameField, new Label("Quantity:"), quantityField,
                new Label("Price:"), priceField, saveButton);

        modalStage.setScene(new Scene(modalLayout));
        modalStage.showAndWait();
    }

    private void addStockItem(String name, int quantity, double price) {
        String query = "INSERT INTO stock (name, quantity, price) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
            loadStockData(); // Reload the data after insertion
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item added successfully!");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding item: " + e.getMessage());
            alert.show();
        }
    }

    private void updateStockItem(int id, String name, int quantity, double price) {
        String query = "UPDATE stock SET name = ?, quantity = ?, price = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            loadStockData(); // Reload the data after updating
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item updated successfully!");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating item: " + e.getMessage());
            alert.show();
        }
    }
}