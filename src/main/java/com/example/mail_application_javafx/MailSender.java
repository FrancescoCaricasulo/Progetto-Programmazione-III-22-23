package com.example.mail_application_javafx;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class MailSender implements Callable<Boolean> {
    private final int serverPort = 2001;
    private Email mailToSend;

    /**
     * Task che richiede l'invio di una mail:
     * @param mailToSend: oggetto email che si vuole inviare */
    public MailSender(Email mailToSend){
        this.mailToSend = mailToSend;
    }

    @Override
    public Boolean call() {
        try (Socket socket = new Socket(InetAddress.getByName(null), serverPort)){
            System.out.println("Socket successfully created");

            /* Se l'utente è in grado di inviare una nuova mail esiste sicuramente */
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.writeObject(mailToSend);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
