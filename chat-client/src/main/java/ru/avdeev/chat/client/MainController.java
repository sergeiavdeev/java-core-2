package ru.avdeev.chat.client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public ListView contactList;

    @FXML
    public TextArea chatArea;

    @FXML
    public TextField messageField;

    @FXML
    public Button btnSend;

    public void connectServer(ActionEvent actionEvent) {
    }

    public void disconnectServer(ActionEvent actionEvent) {
    }

    public void mockAction(ActionEvent actionEvent) {
    }

    public void exit(ActionEvent actionEvent) {
        System.exit(1);
    }

    public void showHelp(ActionEvent actionEvent) {
    }

    public void showAbout(ActionEvent actionEvent) {
    }

    public void sendMessage(ActionEvent actionEvent) {
        var message = messageField.getText();
        if (message.isBlank()) {
            return;
        }
        chatArea.appendText(message + System.lineSeparator());
        messageField.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var contacts = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            contacts.add("Contact#" + (i + 1));
        }
        contactList.setItems(FXCollections.observableList(contacts));
    }
}
