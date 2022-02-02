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
import ru.avdeev.chat.server.utils.Helper;

import java.io.IOException;
import java.net.URL;

import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable, MessageProcessor {

    private NetworkService networkService;
    private String nick;
    private final ObservableSet<String> contacts = FXCollections.observableSet();

    @FXML
    public VBox loginPanel;

    @FXML
    public VBox chatPanel;

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

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
            networkService.sendMessage(Helper.createMessage("/broadcast", message, ""));
        } else {
            networkService.sendMessage(Helper.createMessage("/private", contact, message));
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

        this.networkService = new NetworkService("localhost", 8181, this);
    }

    @Override
    public void processMessage(String message) {
        Platform.runLater(() -> parseIncomingMessage(message));
    }

    private void parseIncomingMessage(String message) {
        var params = Helper.parseMessage(message);
        switch (params[0]) {
            case "/ok" :
                this.nick = params[1];
                loginPanel.setVisible(false);
                chatPanel.setVisible(true);
                ChatApplication.getStage().setTitle("Easy Chat - " + nick);
                break;
            case "/broadcast" :
            case "/private":
                chatArea.appendText(params[1] + ": " + params[2] + System.lineSeparator());
                break;
            case "/error" :
                showError(params[1]);
                System.out.println("got error " + params[1]);
                break;
            case "/online" :
                contacts.add(params[1]);
                break;
            case "/offline" :
                contacts.remove(params[1]);
                break;
        }
    }

    private void showError(String message) {
        var alert = new Alert(Alert.AlertType.ERROR,
                "An error occurred: " + message,
                ButtonType.OK);
        alert.showAndWait();
    }

    public void sendAuth(ActionEvent actionEvent) {
        var login = loginField.getText();
        var password = passwordField.getText();
        if (login.isBlank() || password.isBlank()) {
            return;
        }

        if (!networkService.isConnected()) {
            try {
                networkService.connect();
            } catch (IOException e) {
                e.printStackTrace();
                showError(e.getMessage());

            }
        }
        networkService.sendMessage(Helper.createMessage("/auth", login, password));
    }
}
