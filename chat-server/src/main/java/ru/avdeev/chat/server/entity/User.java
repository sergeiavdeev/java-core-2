package ru.avdeev.chat.server.entity;

public class User {

    private String login;
    private String password;
    private String name;
    private String secret;

    public User(String login, String password, String name, String secret) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.secret = secret;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name.isEmpty() ? login : name;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        User usr = (User)obj;
        return login.equals(usr.login);
    }
}
