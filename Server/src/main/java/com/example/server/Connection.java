package com.example.server;

import com.example.mail_application_javafx.ClientUser;
import com.example.mail_application_javafx.Email;
import com.example.mail_application_javafx.SharedClientUser;
import com.example.mail_application_javafx.SharedEmail;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/* Gestisce le richieste effettuate dai clients */
public class Connection implements Runnable {
    private Server model;

    public Connection(Server model){
        this.model = model;
    }


    @Override
    public void run() {
        try(ServerSocket socket = new ServerSocket(2001)){

            while(true){
                Socket incomingData = socket.accept();
                model.addToLog("Connection accepted: " + incomingData.toString());
                ObjectInputStream inStream = new ObjectInputStream(incomingData.getInputStream());
                Object obj = inStream.readObject();

                if(obj instanceof SharedClientUser){
                    SharedClientUser sharedUser = (SharedClientUser) obj;
                    ClientUser user = (ClientUser)sharedUser;
                    model.addToLog("User: "+ user.getMailId() + " requests for his inbox emails");

                    /* Creo utente a partire da l'user ricevuto dal client */
                    User userFromClient = new User(user.getMailId());
                    if(userFromClient.checkPath()){
                        model.addToLog("User: " + user.getMailId() + " exists");
                        userFromClient.bindEmails();
                        ArrayList<Email> mailList = userFromClient.getEmails();
                        ObjectOutputStream outputStream = new ObjectOutputStream(incomingData.getOutputStream());
                        outputStream.writeObject(mailList);
                        model.addToLog("Sent mail list to user: " + user.getMailId());
                    }else{
                        ObjectOutputStream outputStream = new ObjectOutputStream(incomingData.getOutputStream());
                        outputStream.writeObject(null);
                        model.addToLog("User: "+user.getMailId() + " not exists");
                    }

                } else if (obj instanceof SharedEmail) {
                    SharedEmail e = (SharedEmail)obj;
                    Email emailReceived = (Email)e;
                    model.addToLog("User: "+ e.getFrom() + " sends an email");
                    List<String> receivers = emailReceived.getTo();
                    ArrayList<User> wrongReceivers = new ArrayList<>();

                    for(int i = 0; i < receivers.size(); i++){
                        User u = new User(receivers.get(i));
                        if(!u.checkPath()){
                            wrongReceivers.add(u); /* Lista degli utenti non trovati */
                            receivers.remove(u.getMailID()); /* Lista degli utenti trovati */
                        }
                    }

                    JSONArray jsonReceivers = new JSONArray();
                    for(String s : receivers){ /* Adesso la lista dei desintari è corretta */
                        jsonReceivers.add(s);
                    }

                    for(String s : receivers){
                        User userToDeliver = new User(s);
                        String path = userToDeliver.getEmailPath().getPath();
                        userToDeliver.bindEmails();
                        int numberOfEmail = userToDeliver.getEmails().size();
                        path = path + "/mail" + numberOfEmail + ".json";

                        emailReceived.setFileNameEmail("mail" + numberOfEmail + ".json");
                        JSONArray emailToSave = makeEmail(emailReceived, jsonReceivers);

                        /* Scrivere mail dentro la casella */
                        try(FileWriter file = new FileWriter(path)){
                            file.write(emailToSave.toJSONString());
                            model.addToLog("Mail from user: " + emailReceived.getFrom() + " delivered to: " + s);
                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                    /* Implementare email di ritorno per gli utenti non esistenti se ce ne sono */
                    if(wrongReceivers.size() > 0){
                        /* C'è almeno un utente che non è stato trovato */
                        /* Viene creata una lista di destinatari con un solo elemento (l'utente che ha inviato la mail con destinatari errati) */
                        ArrayList<String> backToUser = new ArrayList<>();
                        backToUser.add(e.getFrom());

                        /* Viene creata una stringa con la lista dei destinatari non trovati */
                        StringBuilder wrongUser = new StringBuilder();
                        for(User u : wrongReceivers){
                            wrongUser.append(u.getMailID()).append(" - ");
                        }

                        /* Viene creata la mail da re-inviare all'utente */
                        User userToDeliver = new User(backToUser.get(0)); /* TO */

                        /* Creazione nome file della mail */
                        String path = userToDeliver.getEmailPath().getPath();
                        userToDeliver.bindEmails();
                        int numberOfEmail = userToDeliver.getEmails().size();
                        path = path + "/mail" + numberOfEmail + ".json";

                        /* Creazione della mail sfruttando il costruttore che consente di indicare il nome avrà il file nella directory */
                        Email emailWrongReceivers = new Email("mail" + numberOfEmail + ".json","server@unito.it", backToUser, "The follow users are not find on our system: " + wrongUser + ". This refer to email with subject: " + emailReceived.getObj(), "Please don't reply to this message", false);
                        JSONArray backToUserJson = new JSONArray();
                        for(String s : receivers){
                            jsonReceivers.add(s);
                        }

                        JSONArray emailToJson = makeEmail(emailWrongReceivers, backToUserJson);

                        try(FileWriter file = new FileWriter(path)){
                            file.write(emailToJson.toJSONString());
                            model.addToLog("Server sends mail back to user for wrong deliverers. To user: " + backToUser.get(0));
                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                }else if(obj instanceof String mailAndOwner){
                    /* Se l'oggetto ricevuto è una stringa, si tratta di una richiesta di cancellazione email */
                    String[] mailAndOwnerArr = mailAndOwner.split(";");
                    String mail = mailAndOwnerArr[0];
                    String owner = mailAndOwnerArr[1];

                    model.addToLog("User request mail deletion: " + owner);

                    User u = new User(owner);
                    String path = u.getEmailPath().getPath() + "/" + mail;

                    FileReader emailInJson = new FileReader(path);
                    JSONParser parser = new JSONParser();
                    JSONArray array = (JSONArray)parser.parse(emailInJson);

                    for(Object email : array){
                        JSONObject jsonObject = (JSONObject) email;
                        JSONObject jsonObjectEmail = (JSONObject) jsonObject.get("Email");
                        jsonObjectEmail.put("Deleted", true);
                    }

                    try(FileWriter file = new FileWriter(path)){
                        file.write(array.toJSONString());
                        model.addToLog("User request mail deletion: " + u.getMailID() + ": deleted");
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }

                    emailInJson.close();

                }else{
                    model.addToLog("Received a stream of unknown type");
                }

                incomingData.close();
                model.addToLog("Closing connection: " + incomingData.toString());
            }

        } catch (ClassNotFoundException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONArray makeEmail(Email e, JSONArray jsonReceivers){
        JSONObject jsonMain = new JSONObject();
        jsonMain.put("Filename", e.getFileNameEmail());
        jsonMain.put("From", e.getFrom());
        jsonMain.put("To", jsonReceivers);
        jsonMain.put("Subject", e.getObj());
        jsonMain.put("Text", e.getMessage());
        jsonMain.put("Deleted", e.getDeleted());
        JSONObject jsonEmail = new JSONObject();
        jsonEmail.put("Email", jsonMain);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonEmail);

        return jsonArray;
    }
}
