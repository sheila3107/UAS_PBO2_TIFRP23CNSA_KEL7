package mycompany.sikasir;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StockItem {
    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty quantity;
    private final DoubleProperty price;

    public StockItem(int id, String name, int quantity, double price) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }

    // Setters (jika perlu)
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setPrice(double price) { this.price.set(price); }

    // Property getters (PENTING untuk TableView)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public IntegerProperty quantityProperty() { return quantity; }
    public DoubleProperty priceProperty() { return price; }
}