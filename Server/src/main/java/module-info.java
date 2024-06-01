module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.example.mail_application_javafx;
    requires json.simple;


    opens com.example.server to javafx.fxml;
    exports com.example.server;
}