package com.example.mail_application_javafx;

import java.io.Serializable;

/* Interfaccia di ClientUser che viene inviata sulla socket */

public interface SharedClientUser extends Serializable{
    public String getMailId();
}
