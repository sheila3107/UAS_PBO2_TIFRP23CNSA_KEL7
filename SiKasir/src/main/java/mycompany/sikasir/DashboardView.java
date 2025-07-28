package mycompany.sikasir;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node; // Penting untuk mengimport Node

public class DashboardView {
    private Stage primaryStage;
    private String username;
    private String role; // Role pengguna (admin/pegawai)
    private BorderPane root; // Root layout untuk DashboardView

    // ObservableList untuk menyimpan item-item dalam keranjang transaksi
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private Label grandTotalLabel; // Label untuk menampilkan total keseluruhan
    private TableView<CartItem> cartTable; // Tabel untuk menampilkan item di keranjang

    // Tambahkan private Stage untuk modal ManageUsersView jika Anda ingin itu menjadi modal
    private Stage manageUsersModalStage; // Untuk jendela modal Manage Users

    public DashboardView(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;

        // Ambil role pengguna dari database
        UserOperations userOperations;
        try {
            userOperations = new UserOperations();
            User user = userOperations.getProfile(username);
            if (user != null) {
                this.role = user.getRole();
                System.out.println("DEBUG (DashboardView-Constructor): Role pengguna dari DB: '" + this.role + "' untuk username: '" + this.username + "'");
            } else {
                System.out.println("DEBUG (DashboardView-Constructor): User '" + username + "' tidak ditemukan di database. Default role ke: 'pegawai'");
                this.role = "pegawai"; // default jika tidak ditemukan
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.role = "pegawai"; // default jika gagal mengambil role
            System.err.println("DEBUG (DashboardView-Constructor): SQL Exception saat mengambil role. Default role ke: '" + this.role + "'. Error: " + e.getMessage());
        }

        System.out.println("DEBUG (DashboardView-Constructor): Role yang FINAL digunakan untuk inisialisasi menu: '" + this.role + "'");

        // Menginisialisasi root BorderPane
        root = new BorderPane();
        initMenu(); // Inisialisasi menu
    }

    // Metode untuk mendapatkan root layout dari DashboardView
    public BorderPane getView() {
        return root;
    }

    // Menginisialisasi menu yang berada di sisi kiri (LEFT) dari BorderPane
    private void initMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 0 1 0 0;"); // Styling menu

        // Header menu
        Label appTitle = new Label("SiKasir");
        appTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 0 0 10px 0;");
        Label welcomeUser = new Label("Halo, " + username + " (" + role + ")");
        welcomeUser.setStyle("-fx-font-size: 14px; -fx-font-style: italic; -fx-padding: 0 0 15px 0;");

        // Opsi umum di menu
        Button logoutButton = new Button("Logout");
        logoutButton.setMaxWidth(Double.MAX_VALUE); // Tombol memenuhi lebar
        logoutButton.setOnAction(e -> {
            LoginView loginView = new LoginView(primaryStage);
            primaryStage.setScene(new Scene(loginView.getView(), 800, 600));
        });

        menu.getChildren().addAll(appTitle, welcomeUser, new Separator(), logoutButton);

        // Tambahkan tombol Riwayat Transaksi untuk kedua peran
        Button transactionHistoryButton = new Button("Riwayat Transaksi");
        transactionHistoryButton.setMaxWidth(Double.MAX_VALUE);
        transactionHistoryButton.setOnAction(e -> openTransactionHistoryWindow());
        menu.getChildren().add(transactionHistoryButton);

        // --- Logika untuk tombol berdasarkan peran (role) ---
        if ("pegawai".equals(role)) {
            System.out.println("DEBUG (initMenu): Kondisi 'role == pegawai' terpenuhi. Menambahkan tombol 'Buat Transaksi' dan 'Lihat Stok'.");

            Button transactionButton = new Button("Buat Transaksi");
            transactionButton.setMaxWidth(Double.MAX_VALUE);
            transactionButton.setOnAction(e -> {
                System.out.println("DEBUG (initMenu): Tombol 'Buat Transaksi' DIKLIK!");
                openTransactionWindow(); // Tampilkan bagian transaksi saat diklik
            });
            menu.getChildren().add(transactionButton);

            Button viewStockButton = new Button("Lihat Stok");
            viewStockButton.setMaxWidth(Double.MAX_VALUE);
            viewStockButton.setOnAction(e -> openViewStockWindow()); // Tampilkan jendela stok untuk pegawai
            menu.getChildren().add(viewStockButton);
        }

        if ("admin".equals(role)) {
            System.out.println("DEBUG (initMenu): Kondisi 'role == admin' terpenuhi. Menambahkan tombol 'Kelola Pengguna' dan 'Kelola Stok'.");

            Button manageUsersButton = new Button("Kelola Pengguna");
            manageUsersButton.setMaxWidth(Double.MAX_VALUE);
            manageUsersButton.setOnAction(e -> {
                System.out.println("DEBUG (initMenu): Tombol 'Kelola Pengguna' diklik!");
                openManageUsersWindow(); // Buka jendela pengelolaan pengguna
            });

            Button manageStockButton = new Button("Kelola Stok");
            manageStockButton.setMaxWidth(Double.MAX_VALUE);
            manageStockButton.setOnAction(e -> {
                System.out.println("DEBUG (initMenu): Tombol 'Kelola Stok' diklik!");
                openManageStockWindow(); // Menampilkan Kelola Stok di dalam area Center
            });

            // Admin juga bisa membuat transaksi jika diinginkan, uncomment baris ini
            Button transactionButton = new Button("Buat Transaksi"); // <-- Admin juga bisa buat transaksi
            transactionButton.setMaxWidth(Double.MAX_VALUE);
            transactionButton.setOnAction(e -> openTransactionWindow());
            menu.getChildren().add(transactionButton); // <-- Tambahkan untuk admin

            menu.getChildren().addAll(manageUsersButton, manageStockButton);
        }

        root.setLeft(menu); // Set menu di sisi kiri BorderPane
        openDashboard(); // Tampilan default adalah dasbor
    }

    // Metode untuk membuka jendela transaksi (di bagian tengah BorderPane)
    private void openTransactionWindow() {
        System.out.println("DEBUG (openTransactionWindow): Membuka jendela transaksi baru...");
        // Inisialisasi ulang cartItems setiap kali membuka jendela transaksi
        cartItems.clear();

        // >>>>>> PERBAIKAN PENTING UNTUK NULLPOINTEREXCEPTION <<<<<<
        // Pastikan cartTable dan grandTotalLabel diinisialisasi di SINI,
        // SEBELUM mereka digunakan (misalnya sebelum updateGrandTotal()).
        cartTable = new TableView<>();
        grandTotalLabel = new Label("Total Keseluruhan: Rp 0.00");
        grandTotalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        // >>>>>> AKHIR PERBAIKAN <<<<<<

        // Sekarang, updateGrandTotal() bisa dipanggil karena grandTotalLabel sudah diinisialisasi
        updateGrandTotal();

        VBox transactionLayout = new VBox(15);
        transactionLayout.setPadding(new Insets(20));
        transactionLayout.setStyle("-fx-background-color: #ffffff;"); // Styling background

        // --- Bagian Input Item Baru ---
        GridPane itemInputGrid = new GridPane();
        itemInputGrid.setHgap(10);
        itemInputGrid.setVgap(10);
        itemInputGrid.setPadding(new Insets(10, 0, 10, 0)); // Padding atas bawah

        TextField itemField = new TextField();
        itemField.setPromptText("Nama Barang");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Jumlah");

        Label priceLabel = new Label("Harga Satuan: -");
        Label subTotalLabel = new Label("Subtotal: -"); // Subtotal per item yang akan ditambahkan

        final double[] currentItemPrice = {0.0}; // harga default untuk item yang sedang diinput
        final int[] currentItemStock = {0}; // stok untuk validasi

        // Ambil harga dan stok dari database saat item diisi
        itemField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement stmt = connection.prepareStatement("SELECT price, quantity FROM stock WHERE name = ?")) {
                    stmt.setString(1, newVal);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        currentItemPrice[0] = rs.getDouble("price");
                        currentItemStock[0] = rs.getInt("quantity");
                        priceLabel.setText("Harga Satuan: Rp " + currentItemPrice[0]);
                        // Update subtotal jika quantity sudah ada
                        try {
                            int qty = Integer.parseInt(quantityField.getText());
                            subTotalLabel.setText("Subtotal: Rp " + (currentItemPrice[0] * qty));
                        } catch (NumberFormatException ignored) { /* Abaikan jika quantity belum valid */ }
                    } else {
                        currentItemPrice[0] = 0.0;
                        currentItemStock[0] = 0;
                        priceLabel.setText("Harga Satuan: Barang tidak ditemukan");
                        subTotalLabel.setText("Subtotal: -");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error mengambil data stok: " + e.getMessage());
                    alert.show();
                }
            } else {
                currentItemPrice[0] = 0.0;
                currentItemStock[0] = 0;
                priceLabel.setText("Harga Satuan: -");
                subTotalLabel.setText("Subtotal: -");
            }
        });

        // Hitung subtotal saat quantity berubah
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                int qty = Integer.parseInt(newVal);
                if (qty <= 0) {
                    subTotalLabel.setText("Subtotal: -");
                    return;
                }
                if (currentItemPrice[0] > 0) { // Pastikan harga sudah ditemukan
                    double subtotal = currentItemPrice[0] * qty;
                    subTotalLabel.setText("Subtotal: Rp " + subtotal);
                } else {
                    subTotalLabel.setText("Subtotal: -");
                }
            } catch (NumberFormatException ex) {
                subTotalLabel.setText("Subtotal: -");
            }
        });

        Button addItemButton = new Button("Tambah ke Keranjang");
        addItemButton.setOnAction(e -> {
            String itemName = itemField.getText();
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Jumlah tidak valid! Masukkan angka.");
                alert.show();
                return;
            }

            if (itemName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Nama barang tidak boleh kosong.");
                alert.show();
                return;
            }
            if (currentItemPrice[0] <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Barang '" + itemName + "' tidak ditemukan atau harga tidak valid.");
                alert.show();
                return;
            }
            if (quantity <= 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Jumlah harus lebih dari 0.");
                alert.show();
                return;
            }
            if (quantity > currentItemStock[0]) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Stok tidak cukup untuk " + itemName + ". Tersedia: " + currentItemStock[0]);
                alert.show();
                return;
            }

            // Cek apakah item sudah ada di keranjang, jika ya update jumlahnya
            boolean found = false;
            for (CartItem item : cartItems) {
                if (item.getItemName().equalsIgnoreCase(itemName)) {
                    // Cek lagi stok total setelah ditambah
                    if ((item.getQuantity() + quantity) > currentItemStock[0]) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Penambahan ini melebihi stok tersedia untuk " + itemName + ".");
                        alert.show();
                        return;
                    }
                    item.setQuantity(item.getQuantity() + quantity);
                    item.setSubtotal(item.getQuantity() * item.getPricePerUnit());
                    cartTable.refresh(); // Penting untuk menyegarkan tampilan TableView
                    found = true;
                    break;
                }
            }
            if (!found) {
                cartItems.add(new CartItem(itemName, quantity, currentItemPrice[0], quantity * currentItemPrice[0]));
            }

            updateGrandTotal();
            itemField.clear();
            quantityField.clear();
            priceLabel.setText("Harga Satuan: -");
            subTotalLabel.setText("Subtotal: -");
            currentItemPrice[0] = 0.0; // Reset harga
            currentItemStock[0] = 0; // Reset stok
        });

        itemInputGrid.add(new Label("Nama Barang:"), 0, 0);
        itemInputGrid.add(itemField, 1, 0);
        itemInputGrid.add(new Label("Jumlah:"), 0, 1);
        itemInputGrid.add(quantityField, 1, 1);
        itemInputGrid.add(priceLabel, 0, 2);
        itemInputGrid.add(subTotalLabel, 1, 2);
        itemInputGrid.add(addItemButton, 1, 3); // Tambahkan tombol

        // --- Bagian Keranjang Belanja ---
        // cartTable sudah diinisialisasi di awal metode
        // cartTable = new TableView<>(); // HAPUS ATAU KOMENTARI BARIS INI
        cartTable.setItems(cartItems);

        TableColumn<CartItem, String> cartItemNameCol = new TableColumn<>("Nama Barang");
        cartItemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<CartItem, Integer> cartQuantityCol = new TableColumn<>("Jumlah");
        cartQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<CartItem, Double> cartPricePerUnitCol = new TableColumn<>("Harga Satuan");
        cartPricePerUnitCol.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));

        TableColumn<CartItem, Double> cartSubtotalCol = new TableColumn<>("Subtotal");
        cartSubtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        cartTable.getColumns().addAll(cartItemNameCol, cartQuantityCol, cartPricePerUnitCol, cartSubtotalCol);
        // Atur agar kolom mengisi lebar tabel
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        Button removeItemButton = new Button("Hapus Item Terpilih");
        removeItemButton.setOnAction(e -> {
            CartItem selectedItem = cartTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                cartItems.remove(selectedItem);
                updateGrandTotal();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Pilih item yang ingin dihapus terlebih dahulu.");
                alert.show();
            }
        });

        // grandTotalLabel sudah diinisialisasi di awal metode
        // grandTotalLabel = new Label("Total Keseluruhan: Rp 0.00"); // HAPUS ATAU KOMENTARI BARIS INI
        // grandTotalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;"); // HAPUS ATAU KOMENTARI BARIS INI

        Button completeTransactionButton = new Button("Selesaikan Transaksi");
        completeTransactionButton.setOnAction(e -> saveMultipleItemsTransaction());


        transactionLayout.getChildren().addAll(
                new Label("Buat Transaksi Baru"),
                new Separator(), // Garis pemisah
                new Label("Tambah Item:"),
                itemInputGrid,
                new Separator(),
                new Label("Keranjang Transaksi:"),
                cartTable,
                new HBox(10, removeItemButton), // Tombol hapus item
                grandTotalLabel,
                completeTransactionButton
        );

        root.setCenter(transactionLayout); // Tampilkan layout di tengah
    }

    // Metode untuk memperbarui total keseluruhan
    private void updateGrandTotal() {
        double total = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        grandTotalLabel.setText(String.format("Total Keseluruhan: Rp %.2f", total));
    }

    // Metode baru untuk menyimpan transaksi dengan banyak item
    private void saveMultipleItemsTransaction() {
        if (cartItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Keranjang transaksi kosong. Tambahkan item terlebih dahulu.");
            alert.show();
            return;
        }

        double grandTotal = cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
        LocalDateTime transactionDate = LocalDateTime.now();
        int transactionId = -1; // Untuk menyimpan ID transaksi utama

        // SQL untuk memasukkan ke tabel `transactions` (induk)
        String insertTransactionSQL = "INSERT INTO transactions (transaction_date, total_amount, cashier_username) VALUES (?, ?, ?)";
        // SQL untuk memasukkan ke tabel `transaction_items` (anak)
        String insertTransactionItemSQL = "INSERT INTO transaction_items (transaction_id, item_name, quantity, price_per_unit, subtotal) VALUES (?, ?, ?, ?, ?)";
        // SQL untuk mengurangi stok
        String updateStockSQL = "UPDATE stock SET quantity = quantity - ? WHERE name = ?";


        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Mulai transaksi database

            // 1. Masukkan ke tabel `transactions` dan dapatkan ID yang dihasilkan
            try (PreparedStatement stmt = connection.prepareStatement(insertTransactionSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setTimestamp(1, Timestamp.valueOf(transactionDate));
                stmt.setDouble(2, grandTotal);
                stmt.setString(3, username); // Simpan username kasir yang melakukan transaksi
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    transactionId = rs.getInt(1); // Ambil ID transaksi yang baru dibuat
                } else {
                    throw new SQLException("Gagal mendapatkan ID transaksi yang dihasilkan.");
                }
            }

            // 2. Masukkan setiap item keranjang ke tabel `transaction_items` dan perbarui stok
            try (PreparedStatement itemStmt = connection.prepareStatement(insertTransactionItemSQL);
                 PreparedStatement stockStmt = connection.prepareStatement(updateStockSQL)) {

                for (CartItem item : cartItems) {
                    itemStmt.setInt(1, transactionId);
                    itemStmt.setString(2, item.getItemName());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPricePerUnit());
                    itemStmt.setDouble(5, item.getSubtotal());
                    itemStmt.addBatch(); // Tambahkan ke batch untuk eksekusi yang efisien

                    stockStmt.setInt(1, item.getQuantity());
                    stockStmt.setString(2, item.getItemName());
                    stockStmt.addBatch(); // Tambahkan ke batch
                }
                itemStmt.executeBatch(); // Eksekusi semua item sekaligus
                stockStmt.executeBatch(); // Eksekusi semua update stok sekaligus
            }

            connection.commit(); // Komit transaksi jika semua berhasil
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transaksi berhasil disimpan!");
            alert.showAndWait();

            // Kosongkan keranjang setelah berhasil disimpan
            cartItems.clear();
            updateGrandTotal();

            // Opsional, segarkan riwayat transaksi jika saat ini ditampilkan
            if (root.getCenter() instanceof TransactionHistoryView) {
                // Pastikan TransactionHistoryView memiliki metode loadTransactions() yang public
                ((TransactionHistoryView) root.getCenter()).loadTransactions(); // Memanggil loadTransactions jika view aktif
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback transaksi jika ada kesalahan
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan transaksi: " + e.getMessage());
            alert.show();
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Kembalikan auto-commit ke true
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method untuk membuka jendela pengelolaan pengguna (admin)
    private void openManageUsersWindow() {
        System.out.println("DEBUG (openManageUsersWindow): Membuka jendela kelola pengguna...");
        ManageUsersView manageUsersView = new ManageUsersView(primaryStage);

        // >>>>>> PERBAIKAN PENTING UNTUK ManageUsersView <<<<<<
        // Berdasarkan kode ManageUsersView Anda yang mengembalikan Scene,
        // kita akan menampilkannya di jendela modal baru.
        // Jika Anda ingin menampilkannya di tengah dashboard, Anda harus
        // mengubah ManageUsersView agar extends VBox/Pane dan getView() mengembalikan 'this'.
        if (manageUsersModalStage == null || !manageUsersModalStage.isShowing()) {
            manageUsersModalStage = new Stage();
            manageUsersModalStage.initOwner(primaryStage);
            manageUsersModalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            manageUsersModalStage.setTitle("Kelola Pengguna");
            // manageUsersView.getView() mengembalikan Scene, jadi ini benar
            manageUsersModalStage.setScene(manageUsersView.getView());
            manageUsersModalStage.showAndWait();
        } else {
            manageUsersModalStage.toFront(); // Bawa ke depan jika sudah terbuka
        }
        // >>>>>> AKHIR PERBAIKAN <<<<<<
    }

    // Method untuk membuka pengelolaan stok di dalam Center (admin)
    private void openManageStockWindow() {
        System.out.println("DEBUG (openManageStockWindow): Membuka jendela kelola stok (admin)...");
        ManageStockView manageStockView = new ManageStockView(primaryStage, role);
        // Karena ManageStockView sudah extends VBox, objeknya sendiri adalah Node
        // >>>>>> Pastikan Anda sudah HAPUS metode getView() dari ManageStockView.java <<<<<<
        // >>>>>> Jika ManageStockView extends VBox, tidak perlu .getView() <<<<<<
        root.setCenter(manageStockView); // Ini yang benar
    }

    // Method untuk membuka tampilan stok untuk pegawai (view-only)
    private void openViewStockWindow() {
        System.out.println("DEBUG (openViewStockWindow): Membuka jendela lihat stok (pegawai)...");
        ManageStockView manageStockView = new ManageStockView(primaryStage, "pegawai"); // Tetapkan role ke "pegawai" untuk view-only
        // Karena ManageStockView sudah extends VBox, objeknya sendiri adalah Node
        // >>>>>> Pastikan Anda sudah HAPUS metode getView() dari ManageStockView.java <<<<<<
        // >>>>>> Jika ManageStockView extends VBox, tidak perlu .getView() <<<<<<
        root.setCenter(manageStockView); // Ini yang benar
    }

    // Metode untuk membuka jendela Riwayat Transaksi
    private void openTransactionHistoryWindow() {
        System.out.println("DEBUG (openTransactionHistoryWindow): Membuka jendela riwayat transaksi...");
        TransactionHistoryView transactionHistoryView = new TransactionHistoryView();
        // >>>>>> Asumsi TransactionHistoryView memiliki metode getView() yang mengembalikan Node (bukan Scene) <<<<<<
        // Jika TransactionHistoryView Anda extends Pane/VBox, maka cukup: root.setCenter(transactionHistoryView);
        root.setCenter(transactionHistoryView); // Ini tetap seperti kode Anda, berasumsi getView() mengembalikan Node
    }

    // Metode untuk membuka dasbor sebagai tampilan default
    private void openDashboard() {
        System.out.println("DEBUG (openDashboard): Membuka tampilan dashboard default...");
        VBox dashboardContent = new VBox(10);
        dashboardContent.setPadding(new Insets(20));
        dashboardContent.setStyle("-fx-background-color: #e0e0e0; -fx-alignment: center;");

        Label welcomeLabel = new Label("Selamat datang di SiKasir!");
        welcomeLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label userRoleLabel = new Label("Anda login sebagai: " + username + " (" + role + ")");
        userRoleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");

        dashboardContent.getChildren().addAll(welcomeLabel, userRoleLabel);
        root.setCenter(dashboardContent); // Atur tampilan dasbor di tengah
    }

    // --- Kelas Model untuk Item Keranjang (Inner Class) ---
    // Ini sudah bagus sebagai inner class.
    public static class CartItem {
        private final StringProperty itemName;
        private final IntegerProperty quantity;
        private final DoubleProperty pricePerUnit;
        private final DoubleProperty subtotal;

        public CartItem(String itemName, int quantity, double pricePerUnit, double subtotal) {
            this.itemName = new SimpleStringProperty(itemName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.pricePerUnit = new SimpleDoubleProperty(pricePerUnit);
            this.subtotal = new SimpleDoubleProperty(subtotal);
        }

        // Getters
        public String getItemName() { return itemName.get(); }
        public int getQuantity() { return quantity.get(); }
        public double getPricePerUnit() { return pricePerUnit.get(); }
        public double getSubtotal() { return subtotal.get(); }

        // Setters (untuk quantity dan subtotal karena bisa diupdate)
        public void setQuantity(int quantity) {
            this.quantity.set(quantity);
            // Saat quantity berubah, subtotal juga harus diupdate secara otomatis
            this.setSubtotal(quantity * getPricePerUnit());
        }

        public void setSubtotal(double subtotal) {
            this.subtotal.set(subtotal);
        }

        // Property Getters (PENTING untuk TableView)
        public StringProperty itemNameProperty() { return itemName; }
        public IntegerProperty quantityProperty() { return quantity; }
        public DoubleProperty pricePerUnitProperty() { return pricePerUnit; }
        public DoubleProperty subtotalProperty() { return subtotal; }
    }
}