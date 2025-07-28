package mycompany.sikasir;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TransactionItemModel {
    private final StringProperty itemName;
    private final IntegerProperty quantity;
    private final DoubleProperty pricePerUnit;
    private final DoubleProperty subtotal;

    public TransactionItemModel(String itemName, int quantity, double pricePerUnit, double subtotal) {
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

    // Property Getters (PENTING untuk TableView)
    public StringProperty itemNameProperty() { return itemName; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty pricePerUnitProperty() { return pricePerUnit; }
    public DoubleProperty subtotalProperty() { return subtotal; }
}

