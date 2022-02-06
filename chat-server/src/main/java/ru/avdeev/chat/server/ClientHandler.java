package ru.avdeev.chat.server;

import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;
import ru.avdeev.chat.server.entity.User;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ClientHandler {

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final Server server;
    private User user;

    public static ClientHandler createInstance(Socket socket, Server server) {
        return new ClientHandler(socket, server);
    }

    private ClientHandler(Socket socket, Server server) {

        this.server = server;

        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handle() {

        new Thread(() -> {
            if (!auth())return;
            System.out.printf("Client auth success with login %s\n", user.getName());
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String message = inputStream.readUTF();
                    handleMessage(message);
                } catch (IOException e) {
                    server.removeClient(this);
                    Thread.currentThread().interrupt();
                }
            }

        }).start();
    }

    public boolean auth() {

        setTimeout();

        System.out.println("Authorization");
        boolean isAuth = false;
        while (true) {
            try {
                String message = inputStream.readUTF();
                System.out.println(message);
                Message inMessage = new Message(message);
                if (inMessage.getType() == MessageType.REQUEST_AUTH &&
                        server.getUserService().auth(inMessage.getParams().get(0), inMessage.getParams().get(1))) {

                    outputStream.writeUTF(
                        new Message(MessageType.RESPONSE_AUTH_OK,
                            new String[]{inMessage.getParams().get(0)}
                        ).toString()
                    );
                    user = server.getUserService().getUser(inMessage.getParams().get(0));
                    server.addClient(this);
                    isAuth = true;
                    break;
                } else {
                    outputStream.writeUTF(
                        new Message(MessageType.RESPONSE_AUTH_ERROR,
                            new String[]{"Auth error", "Wrong login or password"}
                        ).toString()
                    );
                }
            } catch (IOException e) {
                System.out.println("Unknown client disconnected");
                Thread.currentThread().interrupt();
                break;
            }
        }

        return isAuth;
    }

    private void handleMessage(String message) {
        System.out.println(message);
        Message inMessage = new Message(message);
        switch (inMessage.getType()) {
            case SEND_ALL -> server.broadcastMessage(user, inMessage.getParams().get(0));
            case SEND_PRIVATE -> server.privateMessage(
                    user,
                    server.getUserService().getUser(inMessage.getParams().get(0)),
                    inMessage.getParams().get(1)
            );
            default -> {
            }
        }
    }

    public void send(Message message) {
        try {
            outputStream.writeUTF(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    private void setTimeout() {
        new Thread(() -> {
            try {
                sleep(120 * 1000);
                if (user == null) {
                    inputStream.close();
                    outputStream.close();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
