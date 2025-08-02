# Si Kasir

Nama Proyek: **Si Kasir - Sistem Kasir Digital Sederhana untuk Warung UMKM**

### Deskripsi Proyek
Sikasir adalah sistem kasir digital sederhana yang dirancang khusus untuk warung dan pelaku UMKM. Sistem ini bertujuan untuk mempermudah pencatatan transaksi penjualan, pengecekan stok barang, serta pencetakan struk penjualan secara cepat dan efisien.

### Latar Belakang 
Hingga saat ini, banyak warung kecil dan usaha rumahan yang masih melakukan pencatatan transaksi secara manual menggunakan buku tulis. Cara tersebut sering menimbulkan berbagai kendala, seperti kesalahan pencatatan, hilangnya data, serta kesulitan dalam melakukan evaluasi penjualan. Selain itu, ketiadaan sistem pengelolaan stok menyebabkan pemilik usaha kesulitan memantau ketersediaan barang, sehingga berpotensi terjadi kekosongan atau penumpukan barang. Oleh sebab itu, dibutuhkan sebuah solusi digital yang sederhana dan mudah digunakan untuk membantu pencatatan transaksi dan manajemen stok secara lebih tertata dan efisien.

### Fitur Utama

  - **Log In**: Menyajikan fitur log in dengan dua pilihan role, yaitu sebagai admin dan pegawai.
  - **Transaksi Penjualan**: Input data barang yang dibeli pelanggan, perhitungan total harga, serta cetak struk transaksi.
  - **Manajemen Stok Barang**: Menerapkan CRUD Melihat daftar barang, menambah data barang baru, mengedit detail barang, dan menghapus barang.
  - **Histori Transaksi**: Menampilkan riwayat transaksi yang pernah dilakukan, lengkap dengan filter berdasarkan tanggal tertentu.
  - **Manajemen Akun Pegawai (Admin)**: Fitur khusus admin untuk membuat, mengedit, dan menghapus akun pegawai.
  - **Logout**: Fitur untuk keluar dari sistem secara aman dan mengakhiri sesi pengguna.

### Aktor dalam Sistem
  - **Admin**:   - Mengelola akun pengguna
                 - Melakukan transaksi penjualan
  	             - Melihat stok barang
  	             - Melihat histori transaksi
  - **Pegawai**: - Melakukan transaksi penjualan
  	             - Melihat stok barang
  	             - Melihat histori transaksi


### Penerapan Konsep OOP

Aplikasi ini dibangun dengan mengimplementasikan empat pilar utama OOP:

1. **Encapsulation (Enkapsulasi)**
Konsep ini diterapkan dengan menyembunyikan data (variabel) di dalam kelas dan menyediakan method public (getter dan setter) untuk mengakses atau mengubahnya.

```java
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

    // Method public "getter" untuk mengakses data
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public int getQuantity() { return quantity.get(); }
    public double getPrice() { return price.get(); }

    // Method public "setter" untuk mengubah data
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public void setPrice(double price) { this.price.set(price); } 
    }

```
2. **Inheritance (Pewarisan)**
Pewarisan terjadi ketika sebuah kelas mewarisi properti dan method dari kelas lain. Dalam aplikasi JavaFX, ini sering terlihat pada kelas-kelas View atau Controller. App adalah subclass yang mewarisi method dari Application sebagai superclass. Method start() adalah contoh method yang di-override dari Application.

```java
public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // ... (kode implementasi)
    }

    public static void main(String[] args) {
        launch(args);
    }}
```

3. **Abstraction (Abstraksi)**
Abstraksi menyembunyikan kompleksitas detail implementasi dan hanya menunjukkan fungsionalitas yang penting. Di sini, abstraksi terlihat pada penggunaan kelas DatabaseConnection dan UserOperations.


```java
public class LoginView {
    private Stage primaryStage;
    private UserOperations userOperations; // Penggunaan objek UserOperations

    public VBox getView() {
        // ...
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            try {
                // Di sini, LoginView hanya 'tahu' bahwa ada method loginUser()
                // dan tidak perlu tahu bagaimana method tersebut berinteraksi dengan database.
                userOperations = new UserOperations();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (userOperations.loginUser(username, password)) {
                // ...
            }
        });
        // ...
    }}
```
4. **Polymorphism (Polimorfisme)**
Polimorfisme memungkinkan method yang sama memiliki perilaku berbeda tergantung pada objek yang memanggilnya.

```java
public class DatabaseConnection {
    // ...
    public static Connection getConnection() {
        try {
            // ...
            // Method isClosed() dari interface java.sql.Connection
            // dapat memiliki implementasi yang berbeda-beda tergantung driver-nya.
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Reconnected to database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }}

```

### Teknologi yang Digunakan

  * **Bahasa Pemrograman**: Java
  * **Framework UI**: JavaFX
  * **Database**: \[MySQL]
  * **IDE**: \[NetBeans]




### Kontributor

  * Kelompok 7 - UAS PBO2
  * \[Luthfy Arief-23552011045]
  * \[Sheila Nur Hamidah-23552011398]
  * \[Federix Aryansyah-23552011387]
