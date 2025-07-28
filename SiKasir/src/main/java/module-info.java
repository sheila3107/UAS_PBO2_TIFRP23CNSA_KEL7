module mycompany.sikasir {
    requires javafx.controls;
    requires javafx.fxml;
    requires mysql.connector.java;
    requires java.sql;

    opens mycompany.sikasir to javafx.fxml;
    exports mycompany.sikasir;
}
