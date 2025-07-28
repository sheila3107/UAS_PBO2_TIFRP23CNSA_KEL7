  
package mycompany.sikasir;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class TransactionModel {
    private final IntegerProperty transactionId;
    private final ObjectProperty<LocalDateTime> transactionDate;
    private final DoubleProperty totalAmount;
    private final StringProperty cashierUsername;

    public TransactionModel(int transactionId, LocalDateTime transactionDate, double totalAmount, String cashierUsername) {
        this.transactionId = new SimpleIntegerProperty(transactionId);
        this.transactionDate = new SimpleObjectProperty<>(transactionDate);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.cashierUsername = new SimpleStringProperty(cashierUsername);
    }

    // Getters
    public int getTransactionId() { return transactionId.get(); }
    public LocalDateTime getTransactionDate() { return transactionDate.get(); }
    public double getTotalAmount() { return totalAmount.get(); }
    public String getCashierUsername() { return cashierUsername.get(); }

    // Property Getters (PENTING untuk TableView)
    public IntegerProperty transactionIdProperty() { return transactionId; }
    public ObjectProperty<LocalDateTime> transactionDateProperty() { return transactionDate; }
    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public StringProperty cashierUsernameProperty() { return cashierUsername; }
}  

