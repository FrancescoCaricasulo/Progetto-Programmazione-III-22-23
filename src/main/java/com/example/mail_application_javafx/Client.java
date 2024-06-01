package com.example.mail_application_javafx;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/* Model dell'interfaccia principale dell'applicazione */

public class Client {
    private StringProperty mailAddress;
    private ListProperty<Email> inbox;
    private ObservableList<Email> inboxContent;
    private ClientController controller;

    public Client(String emailAddress, ClientController controller){
        this.controller = controller;
        this.mailAddress = new SimpleStringProperty(emailAddress);
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent);
    }

    public ListProperty<Email> inboxProperty(){
        return this.inbox;
    }

    public StringProperty mailAddress(){
        return this.mailAddress;
    }

    public boolean removeMail(Email mailToRemove) throws ExecutionException, InterruptedException {
        /* Invia richiesta di cancellazione mail al server */
        String fileNameEmailToRemove = mailToRemove.getFileNameEmail();
        Deletion dTask = new Deletion(fileNameEmailToRemove + ";" + mailAddress.getValue());

        FutureTask<Boolean> futureTask = new FutureTask<>(dTask);
        Thread t = new Thread(futureTask);
        t.start();

        boolean returnedValue = futureTask.get();
        if(returnedValue){
            this.inboxContent.remove(mailToRemove);
            return true;
        }else {
            return false;
        }
    }

    public boolean getMails() throws ExecutionException, InterruptedException {
        /* Questo metodo richiede mail al server. */
        System.out.println("Creo task");
        Connection cTask = new Connection(mailAddress.getValue());
        FutureTask<ArrayList<Email>> futureTask = new FutureTask<>(cTask);

        Thread requestMailList = new Thread(futureTask);
        requestMailList.start();

        ArrayList<Email> mails = futureTask.get();
        if(mails == null){
            return false;
        }else{
            inbox.removeAll();
            for(Email e : mails){
                if(!e.getDeleted()){
                    inbox.add(e);
                }
            }
            return true;
        }
    }

    public boolean sendReplyMail(Email e) throws ExecutionException, InterruptedException {
        MailSender sendEmail = new MailSender(e);
        FutureTask<Boolean> futureTask = new FutureTask<>(sendEmail);
        Thread t = new Thread(futureTask);
        t.start();

        boolean returnedValue = futureTask.get();
        if(returnedValue){
            controller.clearTextReply();
            return true;
        }else{
            return false;
        }
    }
}
