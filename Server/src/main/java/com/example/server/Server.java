package com.example.server;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/* Model del server */

public class Server {
    private StringProperty label;
    private ListProperty<String> logLines;

    public Server(){
        label = new SimpleStringProperty();
        logLines = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public void addToLog(String line){
        Platform.runLater(() -> {
            logLines.add(line);
        });
    }

    public ListProperty<String> showLog(){
        return logLines;
    }

    public StringProperty getLabel(){
        return label;
    }

    public void listenToConnections(){
        label.set("In ascolto..");
        Thread t = new Thread(new Connection(this));
        t.start();
    }
}
