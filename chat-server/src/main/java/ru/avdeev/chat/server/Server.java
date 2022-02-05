package ru.avdeev.chat.server;

import ru.avdeev.chat.commons.Helper;
import ru.avdeev.chat.commons.Message;
import ru.avdeev.chat.commons.MessageType;
import ru.avdeev.chat.server.auth.UserService;
import ru.avdeev.chat.server.entity.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private final int port;
    private final List<ClientHandler> clients;
    private final UserService userService;

    public Server(int port, UserService userService) {
        this.port = port;
        this.userService = userService;
        clients = new ArrayList<>();
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Server started on port %d\n", port);
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                ClientHandler.createInstance(socket, this).handle();
                System.out.println("Client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
        for (ClientHandler clientHandler : clients) {
            clientHandler.send(new Message(MessageType.USER_ONLINE,
                    new String[]{client.getUser().getName(), client.getUser().getLogin()})
            );

            client.send(
                    new Message(MessageType.USER_ONLINE,
                            new String[]{clientHandler.getUser().getName(), clientHandler.getUser().getLogin()})
            );
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.printf("Client %s disconnected\n", client.getUser().getName());
        for (ClientHandler clientHandler : clients) {
            clientHandler.send(
                    new Message(MessageType.USER_OFFLINE,
                            new String[]{client.getUser().getName(), client.getUser().getLogin()})
            );
        }
    }

    public void broadcastMessage(User sender, String message) {
        for (ClientHandler client : clients) {
            client.send(new Message(MessageType.SEND_ALL,
                    new String[]{sender.toString(), message}));
        }
    }

    public void privateMessage(User sender, User receiver, String message) {

        for (ClientHandler client : clients) {
            if (client.getUser().equals(receiver)) {
                client.send(new Message(MessageType.SEND_PRIVATE,
                        new String[]{sender.toString(), message}));
            }
        }
    }
}
