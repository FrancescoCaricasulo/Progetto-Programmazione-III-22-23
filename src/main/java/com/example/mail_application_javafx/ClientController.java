package com.example.mail_application_javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/* Controller del Client */
public class ClientController {
    @FXML
    public Button btn_refresh;
    @FXML
    public Button btn_delete;
    @FXML
    private Label lbl_from;

    @FXML
    private Label lbl_to;

    @FXML
    private Label lbl_obj;

    @FXML
    private Label lbl_user;

    @FXML
    private TextArea txtarea_content;

    @FXML
    private ListView<Email> lst_emails;

    @FXML
    private Menu fileMenu;

    @FXML
    private TextArea textarea_reply;

    @FXML
    private Button btn_reply;

    @FXML
    private Button btn_forward;

    @FXML
    private TextField textbox_forwarding_users;

    private Client model;
    private Email selectedEmail;
    private Email emptyEmail;

    private final String initialUser;

    public ClientController(String initialUser){
        this.initialUser = initialUser;
    }

    @FXML
    public void initialize() throws ExecutionException, InterruptedException {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        btn_refresh.setOnMouseClicked(event -> {
            try {
                onRefreshButtonClick();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        btn_forward.setOnMouseClicked(event -> {
            try {
                onForwardButtonClick();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        btn_reply.setOnMouseClicked(event -> {
            try {
                onReplyButtonClick();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        btn_delete.setOnMouseClicked(event -> {
            try {
                onDeleteButtonClick();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        fileMenu.setOnAction(event -> {
            try {
                onNewMailButtonClick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        model = new Client(initialUser, this);
        if(!model.getMails()){
            fileMenu.setVisible(false);
        }

        selectedEmail = null; /* Nessuna mail selezionata */

        lst_emails.itemsProperty().bind(model.inboxProperty());
        lst_emails.setOnMouseClicked(this::showSelectedEmail);
        lbl_user.textProperty().bind(model.mailAddress());

        emptyEmail = new Email("","", List.of(""), "", "", false);

        updateDetailView(emptyEmail);
    }

    @FXML
    private void onRefreshButtonClick() throws ExecutionException, InterruptedException {
        lst_emails.getItems().clear();
        if(!model.getMails()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Server offline");
            alert.setContentText("Can't retreive emails because the server is offline");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onDeleteButtonClick() throws ExecutionException, InterruptedException {
        if(model.removeMail(selectedEmail)){
            updateDetailView(emptyEmail);
            if(lst_emails.getItems().isEmpty()){
                textarea_reply.setVisible(false);
                btn_reply.setVisible(false);
                textbox_forwarding_users.setVisible(false);
                btn_forward.setVisible(false);
            }
        }else{
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Server offline");
            error.setContentText("Can't send request for email deletion, try again later");
            error.showAndWait();
        }

    }

    protected void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = lst_emails.getSelectionModel().getSelectedItem();
        selectedEmail = email;
        updateDetailView(email);

        if(email != null){
            textarea_reply.setVisible(true);
            btn_reply.setVisible(true);
            textbox_forwarding_users.setVisible(true);
            btn_forward.setVisible(true);
        }
    }

    protected void updateDetailView(Email email) {
        if(email != null) {
            lbl_from.setText(email.getFrom());
            lbl_to.setText(String.join(", ", email.getTo()));
            lbl_obj.setText(email.getObj());
            txtarea_content.setText(email.getMessage());
        }
    }

    @FXML
    private void onNewMailButtonClick() throws IOException {
        Stage newMailWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newMail.fxml"));
        loader.setController(new NewMailController(lbl_user.textProperty().getValue()));
        Scene scene = new Scene(loader.load(), 640, 400);
        newMailWindow.setTitle("New email");
        newMailWindow.setScene(scene);
        newMailWindow.show();
    }

    @FXML
    private void onReplyButtonClick() throws ExecutionException, InterruptedException {
        if(textarea_reply.textProperty().getValueSafe().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Empty reply");
            alert.setContentText("Can't send email because the reply area is empty");
            alert.showAndWait();
        }else{
            Email email = lst_emails.getSelectionModel().getSelectedItem();
            String text = textarea_reply.textProperty().getValue();
            String replyText = "In origin:\nFrom:" + email.getFrom() + "\nTo:" + email.getTo() + "\nSubject:" + email.getObj() + "\nText:" + email.getMessage() + "\nReply:\n----------\n" + text;
            ArrayList<String> newReceivers = (ArrayList<String>) email.getTo();
            newReceivers.remove(lbl_user.textProperty().getValue());
            newReceivers.add(email.getFrom());
            Email replyEmail = new Email(lbl_user.textProperty().getValue(), newReceivers, replyText, "Replies to: " + email.getObj(), false);
            if(!model.sendReplyMail(replyEmail)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Server offline");
                alert.setContentText("Can't send email because the server is offline");
                alert.showAndWait();
            }else{
                textarea_reply.setText("");
            }
        }
    }

    public void clearTextReply() {
        textarea_reply.setText("");
    }

    public void onForwardButtonClick() throws ExecutionException, InterruptedException {
        if(textbox_forwarding_users.textProperty().getValueSafe().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Empty forwarding list");
            alert.setContentText("Can't forward email because the forwarding list is empty");
            alert.showAndWait();
        }else{
            String[] newReceivers = textbox_forwarding_users.textProperty().getValue().split(";");
            ArrayList<String> listOfNewReceivers = new ArrayList<>(List.of(newReceivers));
            Email email = lst_emails.getSelectionModel().getSelectedItem();
            String text = textarea_reply.textProperty().getValue();
            String replyText = "In origin:\nFrom:" + email.getFrom() + "\nTo:" + email.getTo() + "\nSubject:" + email.getObj() + "\nText:" + email.getMessage() + "\n----------\n" + text;
            Email forwardEmail = new Email(email.getFrom(), listOfNewReceivers, replyText, "Forward from: "+ email.getFrom() + " " + email.getObj(), false);
            if(!model.sendReplyMail(forwardEmail)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Server offline");
                alert.setContentText("Can't send email because the server is offline");
                alert.showAndWait();
            }else{
                textbox_forwarding_users.setText("");
            }
        }
    }
}
