package org.agprogproject;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            // Kullanıcı adı iste
            output.writeUTF("Enter your username: ");
            username = input.readUTF();
            System.out.println("Received username: " + username);

            // Şifre iste
            output.writeUTF("Enter your password: ");
            String password = input.readUTF();
            System.out.println("Received password for username: " + username);

            // Kullanıcı doğrulama
            if (DatabaseManager.loginUser(username, password)) {
                System.out.println("User " + username + " logged in.");
                DatabaseManager.setUserOnline(username, true); // Çevrimiçi durumu güncelle
                Server.addUser(username, socket); // Kullanıcıyı sunucuya ekle
                output.writeUTF("Welcome, " + username + "! You are now online."); // Başarılı giriş yanıtı
            } else {
                output.writeUTF("Invalid username or password.");
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + username);
        } finally {
            disconnectUser();
        }
    }

    // Mesaj gönderme
    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcıyı bağlantıdan çıkarma
    private void disconnectUser() {
        try {
            if (username != null) {
                DatabaseManager.setUserOnline(username, false); // Kullanıcıyı çevrimdışı yap
                Server.removeUser(username); // Kullanıcıyı sunucudan çıkar
                System.out.println("User " + username + " is now offline.");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
