package ru.avdeev.chat.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import ru.avdeev.chat.client.network.MessageProcessor;
import ru.avdeev.chat.client.network.NetworkService;

import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;


import java.net.URL;

import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable, MessageProcessor {

    private NetworkService networkService;
    private String nick;
    private final ObservableSet<String> contacts = FXCollections.observableSet();

    @FXML
    public VBox chatPanel;

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
        String message = messageField.getText();
        if (message.isEmpty()) {
            return;
        }

        String contact = contactList.getSelectionModel().getSelectedItem();
        if (contact == null || contact.equals("ALL")) {
            //networkService.sendMessage(Helper.createMessage("/broadcast", message, ""));
            networkService.sendMessage(new Message(MessageType.SEND_ALL, new String[]{message}));
        } else {
            //networkService.sendMessage(Helper.createMessage("/private", contact, message));
            networkService.sendMessage(new Message(MessageType.SEND_PRIVATE, new String[]{contact, message}));
            chatArea.appendText(nick + ": " + message + System.lineSeparator());
        }

        messageField.clear();
        player.stop();
        player.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        contacts.addListener((Change<? extends String> change) -> {
            if (change.wasAdded()) {
                if (!change.getElementAdded().equals(nick))
                    contactList.getItems().add(change.getElementAdded());
            }
            if (change.wasRemoved()) {
                contactList.getItems().remove(change.getElementRemoved());
            }
        });

        contacts.add("ALL");

        Media sound = new Media(Objects.requireNonNull(getClass().getClassLoader().getResource("sound/switch.mp3")).toString());
        player = new MediaPlayer(sound);

        this.networkService = NetworkService.getInstance();
        this.networkService.addMessageProcessor(this);
    }

    @Override
    public void processMessage(String message) {
        Platform.runLater(() -> parseIncomingMessage(message));
    }

    private void parseIncomingMessage(String message) {
        Message inMessage = new Message(message);
        switch (inMessage.getType()) {
            case RESPONSE_AUTH_OK:
                this.nick = inMessage.getParams().get(0);
                chatPanel.setVisible(true);
                ChatApplication.getStage().setTitle("Easy Chat - " + nick);
                break;
            case SEND_ALL:
            case SEND_PRIVATE:
                chatArea.appendText(inMessage.getParams().get(0) + ": " + inMessage.getParams().get(1) + System.lineSeparator());
                break;
            case USER_ONLINE:
                contacts.add(inMessage.getParams().get(0));
                break;
            case USER_OFFLINE:
                contacts.remove(inMessage.getParams().get(0));
                break;
        }
    }
}
