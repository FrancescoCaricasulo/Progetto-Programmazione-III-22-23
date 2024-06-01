package com.example.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
/* Controller del server */
public class HelloController {
    @FXML
    public ListView<String> logListView;
    @FXML
    private Label welcomeText;

    @FXML
    private Button btn_start;

    Server model;

    @FXML
    public void initialize(){
        if(this.model != null){
            throw new IllegalStateException("Model can only be initialized once");
        }

        btn_start.setOnAction(event -> {
            onStartServerButtonClick();
        });

        model = new Server();
        logListView.itemsProperty().bind(model.showLog());
        welcomeText.textProperty().bind(model.getLabel());
    }

    protected void onStartServerButtonClick() {
        model.listenToConnections();
    }

    protected void closeThread(){
        /* Chiusura dell'app (thread incluso) */
        Platform.exit();
        System.exit(0);
    }

}