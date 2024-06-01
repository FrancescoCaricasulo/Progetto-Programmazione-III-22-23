package com.example.mail_application_javafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/* Model dell'interfaccia che consente la creazione (e l'invio) di una nuova mail */
public class NewMailModel {
    private StringProperty sendToString;
    private StringProperty subjectString;
    private StringProperty bodyString;

    public NewMailModel(){
        sendToString = new SimpleStringProperty();
        subjectString = new SimpleStringProperty();
        bodyString = new SimpleStringProperty();
    }

    public String getSendToString(){
        return sendToString.getValue();
    }

    public String getSubjectString(){
        return subjectString.getValue();
    }

    public String getBodyString(){
        return bodyString.getValue();
    }

    public boolean sendMail(Email e) throws ExecutionException, InterruptedException {
        MailSender sendEmail = new MailSender(e);
        FutureTask<Boolean> futureTask = new FutureTask<>(sendEmail);
        Thread t = new Thread(futureTask);
        t.start();

        return futureTask.get();
    }
}
