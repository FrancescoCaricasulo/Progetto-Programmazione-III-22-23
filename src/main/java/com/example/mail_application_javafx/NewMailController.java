package com.example.mail_application_javafx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/* Controller dell'interfaccia che consente la creazione (e l'invio) di una nuova mail */
public class NewMailController {
    private String mailIdOwner;
    @FXML
    private TextArea bodyTextBox;

    @FXML
    private TextField sendToTextBox;

    @FXML
    private TextField subjectTextBox;

    @FXML
    private Button sendEmailButton;

    private NewMailModel model;
    public NewMailController(String mailIdOwner){
        this.mailIdOwner = mailIdOwner;
    }

    @FXML
    public void initialize(){
        if(this.model != null){
            throw new IllegalStateException("Model can only be initialized once");
        }
        sendEmailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    onSendEmailButton();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        model = new NewMailModel();
    }

    @FXML
    private void onSendEmailButton() throws ExecutionException, InterruptedException {
        if(bodyTextBox.textProperty().getValueSafe().isEmpty() || sendToTextBox.textProperty().getValueSafe().isEmpty() || subjectTextBox.textProperty().getValueSafe().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Void fields");
            alert.setContentText("Can't send email with empty fields: check body, or receivers, or subject");
            alert.showAndWait();
        }else{
            /* Se i campi sono stati riempiti correttamente la mail viene inviata. */
            String[] receivers = sendToTextBox.textProperty().getValue().split(";");
            ArrayList<String> receiversArr = new ArrayList<>(Arrays.asList(receivers));
            Email newEmail = new Email(mailIdOwner, receiversArr, bodyTextBox.textProperty().getValue(), subjectTextBox.textProperty().getValue(), false);

            if(!model.sendMail(newEmail)){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Server offline");
                alert.setContentText("Can't send email because server is offline");
                alert.showAndWait();
            }else{
                Stage stage = (Stage) sendEmailButton.getScene().getWindow();
                stage.close();
            }
        }
    }

}
