package ru.avdeev.chat.server;

import ru.avdeev.chat.server.entity.User;
import ru.avdeev.chat.server.utils.Helper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private final Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final Server server;
    private User user;

    public static ClientHandler createInstance(Socket socket, Server server) {
        return new ClientHandler(socket, server);
    }

    private ClientHandler(Socket socket, Server server) {

        this.socket = socket;
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
            auth();
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

    public void auth() {

        System.out.println("Authorization");
        while (true) {
            try {
                String message = inputStream.readUTF();
                String[] params = Helper.parseMessage(message);
                if (params[0].equals("/auth") && server.getUserService().auth(params[1], params[2])) {
                    outputStream.writeUTF(Helper.createMessage("/ok", params[1], ""));
                    user = server.getUserService().getUser(params[1]);
                    server.addClient(this);
                    break;
                } else {
                    outputStream.writeUTF(
                        Helper.createMessage("/error", "Auth error", "Wrong login or password")
                    );
                }
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(String message) {
        System.out.println(message);
        String[] params = Helper.parseMessage(message);
        switch (params[0]) {
            case "/broadcast":
                server.broadcastMessage(user, params[1]);
                break;
            case "/private":
                server.privateMessage(user, server.getUserService().getUser(params[1]), params[2]);
                break;
            default:
                break;
        }
    }

    public void send(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }
}
