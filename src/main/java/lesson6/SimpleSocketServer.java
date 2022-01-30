package lesson6;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class SimpleSocketServer {

    private static final int PORT = 8181;
    private final Map<String, Client> clients = new HashMap<>();

    private static class Client {

        public Socket socket;
        DataInputStream in;
        DataOutputStream out;

        public Client(Socket socket, DataInputStream in, DataOutputStream out) {
            this.socket = socket;
            this.in = in;
            this.out = out;
        }
    }

    public static void main(String[] args) {
        new SimpleSocketServer().start();
    }

    public void start() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            consoleService();

            while (!Thread.currentThread().isInterrupted()) {

                Socket socket = server.accept();
                new Thread(() -> {
                    String login = login(socket);
                    if (login != null) {
                        new Thread(() -> clientService(login)).start();
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String login(Socket socket) {

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            while (!Thread.currentThread().isInterrupted()) {
                Map<String, String> params = getMessageParams(in.readUTF());
                String login = params.get("login");
                if (login != null && !login.isEmpty()) {
                    clients.put(login, new Client(socket, in, out));
                    out.writeUTF("Client connected with login " + login);
                    System.out.printf("\nClient connected with login %s\n", login);
                    System.out.print(">>> ");
                    return login;
                }
                out.writeUTF("Not registered!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void clientService(String login) {

        try {
            Client client = clients.get(login);
            DataInputStream in = client.in;
            DataOutputStream out = client.out;

            while (!Thread.currentThread().isInterrupted()) {
                String message = in.readUTF();

                Map<String, String> params = getMessageParams(message);

                System.out.printf("\nReceived message fom %s: %s\n", login, (params.size() == 0 ? message : params));
                if (message.equals("quit")) {
                    Thread.currentThread().interrupt();
                }
                if (message.equals("clients")) {
                    out.writeUTF(clients.toString());
                }
                if (params.size() > 0) {
                    sendMessage(params, login);
                }
                System.out.print(">>> ");
            }
            disconnectClient(login);
            System.out.printf("\nClient %s disconnected\n", login);
            System.out.print(">>> ");
        } catch (SocketException e) {
            disconnectClient(login);
            System.out.printf("\nClient %s disconnected\n", login);
            System.out.print(">>> ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Map<String, String> params, String login) throws IOException {

        String receiverName = params.get("receiver");
        String message = params.get("message");
        if (receiverName != null && message != null) {

            if (receiverName.equals("ALL")) {
                sendMessageToAll(message, login);
            } else {
                DataOutputStream out = clients.get(receiverName).out;
                out.writeUTF("sender=" + login + ",message=" + message);
            }
        }
    }

    private void sendMessageToAll(String message, String login) {

        clients.forEach((key, value) -> {
            DataOutputStream out = clients.get(key).out;
            try {
                out.writeUTF("sender=" + login + ",message=" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void consoleService() {

        new Thread(() -> {
            System.out.print(">>> ");
            Scanner scanner = new Scanner(System.in);

            while (!Thread.currentThread().isInterrupted()) {

                String message = scanner.nextLine();
                if (message.equals("clients")) {
                    System.out.println(clients);
                    System.out.print(">>> ");
                }
            }
            scanner.close();

        }).start();
    }

    private void disconnectClient(String login) {

        Client client = clients.get(login);
        if (client == null)return;
        try {
            client.in.close();
            client.out.close();
            client.socket.close();
            clients.remove(login);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getMessageParams(String message) {

        Map<String, String> map = new HashMap<>();

        String[] args = message.split(",");
        for (String arg : args) {
            String[] kv = arg.split("=");
            if (kv.length != 2) continue;
            map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }
}
