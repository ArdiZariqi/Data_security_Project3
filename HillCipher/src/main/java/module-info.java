module com.example.hillcipher {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.hillcipher to javafx.fxml;
    exports com.example.hillcipher;
}