package com.example.mail_application_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String initialUser = getParameters().getRaw().get(0);
        URL clientUrl = Main.class.getResource("view.fxml");
        ClientController controller = new ClientController(initialUser);
        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 740, 575);
        stage.setTitle("Email client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if(args.length > 0){
            launch(args[0]);
        }else{
            launch("francesco@unito.it");
        }
    }
}
