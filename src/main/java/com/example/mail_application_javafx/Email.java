package com.example.mail_application_javafx;

import java.util.ArrayList;
import java.util.List;

public class Email implements SharedEmail{
    private String from;
    private List<String> to;
    private String message;
    private String obj;

    private String fileNameEmail;

    private boolean deleted;

    /**
     * Costruttore #1
     * @param sender email del mittente
     * @param receivers emails dei riceventi
     * @param message contenuto mail
     * @param obj oggetto della mail
     * @param deleted se la mail è stata cancellata
     * */

    public Email(String sender, List<String> receivers, String message, String obj, boolean deleted) {
        from = sender;
        to = new ArrayList<>(receivers);
        this.message = message;
        this.obj = obj;
        this.deleted = deleted;
    }

    /**
     * Costruttore #2
     * @param fileNameEmail nome del file della mail (sul server)
     * @param sender email del mittente
     * @param receivers emails dei riceventi
     * @param message contenuto mail
     * @param obj oggetto della mail
     * */

    public Email(String fileNameEmail, String sender, List<String> receivers, String message, String obj, boolean deleted) {
        from = sender;
        to = new ArrayList<>(receivers);
        this.message = message;
        this.obj = obj;
        this.deleted = deleted;
        this.fileNameEmail = fileNameEmail;
    }

    /* Ritorna il body della mail */
    public String getMessage() {
        return message;
    }

    /* Ritorna la lista di destinatari */
    public List<String> getTo() {
        return to;
    }

    /* Ritorna il mittente */
    public String getFrom() {
        return from;
    }

    /* Ritorna l'oggetto */
    public String getObj() {
        return obj;
    }

    /* Setta il nome del file della mail */
    public void setFileNameEmail(String fileNameEmail) {
        this.fileNameEmail = fileNameEmail;
    }

    /* Ritorna il nome del file della mail */
    public String getFileNameEmail() {
        return fileNameEmail;
    }

    /* Ritorna un booleano che corrisponde con lo stato di cancellazione della mail */
    public boolean getDeleted(){
        return deleted;
    }

    @Override
    public String toString() {
        return from.toString() + " - " + obj.toString();
    }
}
