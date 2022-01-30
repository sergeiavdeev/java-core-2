package lesson6;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;

public class SimpleSocketClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8181;
    private DataOutputStream out;
    private final ThreadLocal<DataInputStream> in = new ThreadLocal<>(); //Idea подсказала, но что это значит не понятно
    private Thread consoleService;

    public static void main(String[] args) {

        new SimpleSocketClient().start();
    }

    public void start() {

        try(Socket socket = new Socket(HOST, PORT)) {
            in.set(new DataInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            System.out.printf("Connected to server %s on port %d.\n", HOST, PORT);

            consoleService().start();

            while(!consoleService.isInterrupted()) {
                String message = in.get().readUTF();
                Map<String, String> params = SimpleSocketServer.getMessageParams(message);
                String sender = params.get("sender");
                String text = params.get("message");
                if (sender != null && text != null) {
                    System.out.printf("\nReceive message from %s: %s\n", sender, text);
                } else {
                    System.out.printf("\n%s\n", message);
                }
                System.out.print("Enter message >>> ");
            }
        } catch (SocketException e) {
            System.out.printf("Connection failed on %s port %d", HOST, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private Thread consoleService() {
        consoleService = new Thread(() -> {
            //BufferedReader грузит процессор, поэтому Scanner
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Enter message >>> ");
                while (!Thread.currentThread().isInterrupted()) {

                    String clientMessage = scanner.nextLine();
                    if (clientMessage.equalsIgnoreCase("quit")) {
                        out.writeUTF("quit");
                        shutdown();
                    }
                    out.writeUTF(clientMessage);
                    System.out.print("Enter message >>> ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return consoleService;
    }

    private void shutdown() {
        if (consoleService.isAlive()) {
            consoleService.interrupt();
        }
        System.out.println("Client stopped");
        System.exit(1);
    }
}
