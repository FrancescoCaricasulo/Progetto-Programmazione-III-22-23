package com.example.mail_application_javafx;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Deletion implements Callable<Boolean> {
    private String message;
    private final int serverPort = 2001;

    /**
     * Task che richiede la cancellazione di una mail:
     * @param message: nome del file della mail che si vuole cancellare */
    public Deletion(String message){
        this.message = message;
    }

    @Override
    public Boolean call() {
        try (Socket socket = new Socket(InetAddress.getByName(null), serverPort)){
            System.out.println("Socket successfully created");

            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.writeObject(message);

            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
