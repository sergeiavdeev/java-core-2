package ru.avdeev.chat.server;

import ru.avdeev.chat.commons.PropertyReader;
import ru.avdeev.chat.server.auth.InMemoryUserService;

public class ChatServerApplication {
    public static void main(String[] args) {

        InMemoryUserService users = new InMemoryUserService();
        users.addUser("avdey", "123");
        users.addUser("halk", "456");
        users.addUser("hook", "789");

        new Server(
                Integer.parseInt(PropertyReader.getInstance().get("port")),
                users
        ).start();
    }
}
