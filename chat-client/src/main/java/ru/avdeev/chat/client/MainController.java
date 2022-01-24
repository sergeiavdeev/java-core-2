package ru.avdeev.chat.client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public ListView<String> contactList;

    @FXML
    public TextArea chatArea;

    @FXML
    public TextField messageField;

    @FXML
    public Button btnSend;

    private MediaPlayer player;

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

        var contact = contactList.getSelectionModel().getSelectedItem();
        if (contact == null || contact.equals("ALL")) {
            contact = "ALL";
        }

        chatArea.appendText(contact + ": " + message + System.lineSeparator());
        messageField.clear();
        player.stop();
        player.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var contacts = new ArrayList<String>();
        contacts.add("ALL");
        for (int i = 0; i < 10; i++) {
            contacts.add("Contact#" + (i + 1));
        }
        contactList.setItems(FXCollections.observableList(contacts));

        Media sound = new Media(Objects.requireNonNull(getClass().getClassLoader().getResource("sound/switch.mp3")).toString());
        player = new MediaPlayer(sound);
    }
}
