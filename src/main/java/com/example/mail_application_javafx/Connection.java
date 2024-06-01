package com.example.mail_application_javafx;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Connection implements Callable<ArrayList<Email>> {
    public static final int serverPort = 2001;
    private String mailAddress;

    /**
     * Task che richiede la lista di email al server:
     * @param mailAddress: indirizzo email del richiedente */
    public Connection(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    @Override
    public ArrayList<Email> call() {
        try (Socket socket = new Socket(InetAddress.getByName(null), serverPort)){
            System.out.println("Socket successfully created");

            /* Chiedo al server se l'utente in questione esiste */
            ClientUser user = new ClientUser(mailAddress);
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.writeObject(user);

            ArrayList<Email> mailList = new ArrayList<>();
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            Object obj = inStream.readObject();

            if(obj instanceof ArrayList){
                ArrayList<?> list = (ArrayList<?>) obj;
                for(Object o : list){
                    if(o instanceof SharedEmail){
                        mailList.add((Email) o);
                    }
                }

                return mailList;
            }else if(obj == null){
                System.out.println("call in Connection: Utente non trovato");
                return null;
            } else {
                System.out.println("Wrong type");
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }


}