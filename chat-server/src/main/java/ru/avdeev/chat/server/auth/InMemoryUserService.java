package ru.avdeev.chat.server.auth;

import ru.avdeev.chat.commons.Helper;
import ru.avdeev.chat.server.entity.User;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUserService implements UserService{

    Map<String, User> users;

    public InMemoryUserService() {
        users = new HashMap<>();
        addUser("avdey", "123");
        addUser("halk", "456");
        addUser("hook", "789");
    }

    @Override
    public User getUser(String login) {
        return users.get(Helper.getSHA256(login));
    }

    @Override
    public User addUser(String login, String password) {
        users.put(Helper.getSHA256(login), new User(Helper.getSHA256(login), Helper.getSHA256(password), login, "secret"));
        return getUser(login);
    }

    @Override
    public void deleteUser(String login) {
        users.remove(Helper.getSHA256(login));
    }

    @Override
    public boolean auth(String login, String password) {

        User user = getUser(login);
        return user != null && user.getPassword().equals(Helper.getSHA256(password));
    }
}
