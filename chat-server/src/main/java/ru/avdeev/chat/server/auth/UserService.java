package ru.avdeev.chat.server.auth;

import ru.avdeev.chat.server.entity.User;

public interface UserService {

    public User getUser(String login);
    public User addUser(String login, String password);
    public void deleteUser(String login);
    public boolean auth(String login, String password);
}
