package org.example.PostAPI.Models;

public class User {
    public int id;
    public String username;
    public String password;

    public int salt;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(String username, String password, int salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }
}
