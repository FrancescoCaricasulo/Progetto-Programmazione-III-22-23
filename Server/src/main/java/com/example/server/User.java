package com.example.server;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.mail_application_javafx.Email;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/* Classe del server per gestire gli utenti che fanno uso del servizio di mailing */
public class User {
    private final String mailID;
    private File emailPath;
    private ArrayList<Email> emails;

    public User(String mailID){
        this.mailID = mailID;
        File src = new File(".");
        File parentSrc = src.getParentFile();
        File users = new File(parentSrc, "Users");
        emailPath = new File(users, mailID);
    }

    public String getMailID() {
        return mailID;
    }

    public File getEmailPath(){
        if(checkPath())
            return emailPath;
        else return null;
    }

    public boolean checkPath(){
        return emailPath.exists();
    }

    public void bindEmails() {
        ArrayList<Email> listEmails = new ArrayList<>();
        if(checkPath()){
            for(File jsonEmail : emailPath.listFiles()){
                try {
                    FileReader fr = new FileReader(jsonEmail);
                    Email e = jsonToEmail(fr);
                    listEmails.add(e);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        emails = listEmails;
    }

    public ArrayList<Email> getEmails(){
        return emails;
    }

    public Email jsonToEmail(FileReader jsonEmail){
        String from = null, subject = null, text = null, fileNameEmail = null;
        boolean read = false, deleted = false;
        List<String> toList = null ;

        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(jsonEmail);

            for(Object obj : array) {
                JSONObject jsonObject = (JSONObject) obj;
                JSONObject jsonObjectContent = (JSONObject) jsonObject.get("Email");
                fileNameEmail = (String) jsonObjectContent.get("Filename");
                from = (String) jsonObjectContent.get("From");
                subject = (String) jsonObjectContent.get("Subject");
                text = (String) jsonObjectContent.get("Text");
                deleted = (boolean)jsonObjectContent.get("Deleted");

                JSONArray toArray = (JSONArray) jsonObjectContent.get("To");
                Iterator<String> iterator = toArray.iterator();
                toList = new ArrayList<>();
                while (iterator.hasNext()) {
                    toList.add(iterator.next());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(from != null && subject != null && text != null && toList != null) {
            return new Email(fileNameEmail, from, toList, text, subject, deleted);
        }else return null;
    }
}
