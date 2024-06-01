package com.example.mail_application_javafx;

import java.io.Serializable;
import java.util.List;

/* Interfaccia di Email che viene inviata sulla socket */

public interface SharedEmail extends Serializable {
    public String getMessage();

    public List<String> getTo();

    public String getFrom();

    public String getObj();
}
