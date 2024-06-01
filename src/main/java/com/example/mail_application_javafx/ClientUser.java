package com.example.mail_application_javafx;

public class ClientUser implements SharedClientUser{
    private String mailId;

    /**
     * Classe utilizzata per comunicare l'indirizzo dell'utente che richiede
     * la lista di email al server
     * @param mailId: indirizzo utente corrente */
    public ClientUser(String mailId){
        this.mailId = mailId;
    }
    @Override
    public String getMailId() {
        return mailId;
    }
}
