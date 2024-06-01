package com.example.server;

import com.example.mail_application_javafx.SharedClientUser;

public class ClientUser implements SharedClientUser {
    private String mailId;

    /**
     * Classe utilizzata per ricevere l'indirizzo dell'utente che richiede
     * la lista di email al server
     * @param mailId: indirizzo utente richiedente */
    public ClientUser(String mailId){
        this.mailId = mailId;
    }
    @Override
    public String getMailId() {
        return null;
    }
}
