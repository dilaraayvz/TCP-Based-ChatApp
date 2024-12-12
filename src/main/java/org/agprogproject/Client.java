package org.agprogproject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void startClient(String username, String password) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connected to server...");
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Sunucudan "Enter your username:" bekle
            String serverMessage = input.readUTF();
            if (serverMessage.equals("Enter your username:")) {
                System.out.println("Server: " + serverMessage);
                output.writeUTF(username); // Kullanıcı adını gönder
            }

            // Sunucudan "Enter your password:" bekle
            serverMessage = input.readUTF();
            if (serverMessage.equals("Enter your password:")) {
                System.out.println("Server: " + serverMessage);
                output.writeUTF(password); // Şifreyi gönder
            }

            // Giriş işleminin sonucunu bekle
            serverMessage = input.readUTF();
            if (serverMessage.startsWith("Welcome")) {
                System.out.println(serverMessage);
            } else {
                System.out.println("Login failed: " + serverMessage);
            }
        } catch (IOException e) {
            System.out.println("Error: Could not connect to the server.");
            e.printStackTrace();
        }
    }
}
