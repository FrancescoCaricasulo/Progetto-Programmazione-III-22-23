package com.example.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        URL clientUrl = HelloController.class.getResource("hello-view.fxml");
        HelloController controller = new HelloController();
        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 500);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            controller.closeThread();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}