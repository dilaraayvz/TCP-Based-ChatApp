package org.agprogproject;

public class User {
    private String username;
    private String password;
    private boolean isOnline;

    public User(String username, String password, boolean isOnline) {
        this.username = username;
        this.password = password;
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
}
