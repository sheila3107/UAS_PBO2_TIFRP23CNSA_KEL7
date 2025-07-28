package mycompany.sikasir;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionHistoryView extends VBox {

    // Menggunakan TransactionModel
    private TableView<TransactionModel> transactionTable;
    private ObservableList<TransactionModel> transactionList;

    public TransactionHistoryView() {
        setPadding(new Insets(20));
        setSpacing(10);

        Label titleLabel = new Label("Riwayat Transaksi");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        transactionTable = new TableView<>();
        transactionList = FXCollections.observableArrayList();

        // Definisikan kolom tabel untuk TransactionModel
        TableColumn<TransactionModel, Integer> idColumn = new TableColumn<>("ID Transaksi");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        TableColumn<TransactionModel, LocalDateTime> transactionDateColumn = new TableColumn<>("Tanggal Transaksi");
        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        TableColumn<TransactionModel, Double> totalAmountColumn = new TableColumn<>("Total Jumlah");
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        TableColumn<TransactionModel, String> cashierUsernameColumn = new TableColumn<>("Kasir");
        cashierUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("cashierUsername"));
        
        // Tambahkan Kolom Aksi (Detail)
        TableColumn<TransactionModel, Void> actionColumn = new TableColumn<>("Aksi");
        actionColumn.setPrefWidth(120);
        actionColumn.setResizable(false);
        actionColumn.setSortable(false);

        actionColumn.setCellFactory(param -> new TableCell<TransactionModel, Void>() {
            private final Button detailButton = new Button("Lihat Detail");

            {
                detailButton.setOnAction(event -> {
                    TransactionModel data = getTableView().getItems().get(getIndex());
                    showTransactionDetail(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailButton);
                }
            }
        });

        transactionTable.getColumns().addAll(idColumn, transactionDateColumn, totalAmountColumn, cashierUsernameColumn, actionColumn);
        transactionTable.setItems(transactionList);
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        getChildren().addAll(titleLabel, transactionTable);

        loadTransactions();
    }

    public void loadTransactions() {
        transactionList.clear();
        String query = "SELECT transaction_id, transaction_date, total_amount, cashier_username FROM transactions ORDER BY transaction_date DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                Timestamp transactionTimestamp = rs.getTimestamp("transaction_date");
                LocalDateTime transactionDate = transactionTimestamp.toLocalDateTime();
                double totalAmount = rs.getDouble("total_amount");
                String cashierUsername = rs.getString("cashier_username");

                // Menggunakan TransactionModel
                transactionList.add(new TransactionModel(transactionId, transactionDate, totalAmount, cashierUsername));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading transactions: " + e.getMessage());
            alert.show();
        }
    }

    // Metode untuk Menampilkan Detail Transaksi, sekarang menggunakan TransactionModel
    private void showTransactionDetail(TransactionModel transaction) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("Detail Transaksi #" + transaction.getTransactionId());

        VBox detailLayout = new VBox(10);
        detailLayout.setPadding(new Insets(20));

        Label headerLabel = new Label("Detail Transaksi");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label transactionIdLabel = new Label("ID Transaksi: " + transaction.getTransactionId());
        Label dateLabel = new Label("Tanggal: " + transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Label cashierLabel = new Label("Kasir: " + transaction.getCashierUsername());
        Label totalLabel = new Label(String.format("Total: Rp %.2f", transaction.getTotalAmount()));
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Menggunakan TransactionItemModel
        TableView<TransactionItemModel> itemDetailTable = new TableView<>();
        ObservableList<TransactionItemModel> itemDetailList = FXCollections.observableArrayList();

        TableColumn<TransactionItemModel, String> itemNameCol = new TableColumn<>("Nama Barang");
        itemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<TransactionItemModel, Integer> itemQuantityCol = new TableColumn<>("Jumlah");
        itemQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<TransactionItemModel, Double> itemPricePerUnitCol = new TableColumn<>("Harga Satuan");
        itemPricePerUnitCol.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));

        TableColumn<TransactionItemModel, Double> itemSubtotalCol = new TableColumn<>("Subtotal");
        itemSubtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        itemDetailTable.getColumns().addAll(itemNameCol, itemQuantityCol, itemPricePerUnitCol, itemSubtotalCol);
        itemDetailTable.setItems(itemDetailList);
        itemDetailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Load item details from database
        loadTransactionItems(transaction.getTransactionId(), itemDetailList);

        Button printReceiptButton = new Button("Cetak Struk");
        printReceiptButton.setOnAction(e -> printReceipt(transaction, itemDetailList));

        detailLayout.getChildren().addAll(
            headerLabel,
            new Separator(),
            transactionIdLabel,
            dateLabel,
            cashierLabel,
            itemDetailTable,
            totalLabel,
            printReceiptButton
        );

        Scene scene = new Scene(detailLayout, 500, 400);
        detailStage.setScene(scene);
        detailStage.showAndWait();
    }

    // Metode untuk Memuat Detail Item Transaksi, sekarang menggunakan TransactionItemModel
    private void loadTransactionItems(int transactionId, ObservableList<TransactionItemModel> itemDetailList) {
        itemDetailList.clear();
        String query = "SELECT item_name, quantity, price_per_unit, subtotal FROM transaction_items WHERE transaction_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                double pricePerUnit = rs.getDouble("price_per_unit");
                double subtotal = rs.getDouble("subtotal");
                itemDetailList.add(new TransactionItemModel(itemName, quantity, pricePerUnit, subtotal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error loading transaction items: " + e.getMessage());
            alert.show();
        }
    }

    // Metode untuk Cetak Struk (Sederhana dalam Dialog), sekarang menggunakan TransactionModel dan TransactionItemModel
    private void printReceipt(TransactionModel transaction, ObservableList<TransactionItemModel> items) {
        StringBuilder receiptContent = new StringBuilder();
        receiptContent.append("========== STRUK PEMBELIAN ==========\n");
        receiptContent.append("ID Transaksi: #").append(transaction.getTransactionId()).append("\n");
        receiptContent.append("Tanggal: ").append(transaction.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        receiptContent.append("Kasir: ").append(transaction.getCashierUsername()).append("\n");
        receiptContent.append("-------------------------------------\n");
        receiptContent.append(String.format("%-20s %5s %10s %10s\n", "Nama Barang", "Qty", "Harga", "Subtotal"));
        receiptContent.append("-------------------------------------\n");

        for (TransactionItemModel item : items) {
            receiptContent.append(String.format("%-20s %5d %10.2f %10.2f\n",
                item.getItemName(),
                item.getQuantity(),
                item.getPricePerUnit(),
                item.getSubtotal()));
        }

        receiptContent.append("-------------------------------------\n");
        receiptContent.append(String.format("Total Pembelian: Rp %.2f\n", transaction.getTotalAmount()));
        receiptContent.append("=====================================\n");
        receiptContent.append("Terima kasih telah berbelanja!\n");

        // Menampilkan struk di Dialog Sederhana
        TextArea textArea = new TextArea(receiptContent.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        Alert receiptAlert = new Alert(Alert.AlertType.INFORMATION);
        receiptAlert.setTitle("Struk Transaksi #" + transaction.getTransactionId());
        receiptAlert.setHeaderText("Struk Pembelian");
        receiptAlert.getDialogPane().setContent(textArea);
        receiptAlert.setResizable(true);
        receiptAlert.showAndWait();
    }
}