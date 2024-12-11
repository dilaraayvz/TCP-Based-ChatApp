package org.agprogproject;

import java.io.*;
import java.net.*;

public class Server {

    private static final int PORT = 12345;

    public static void startServer() {
        // Veritabanı bağlantısını başlat
        DatabaseManager.connect();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.close();
        }
    }

    // Her istemci için bağlantı yönetimi
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private DataInputStream input;
        private DataOutputStream output;
        private int userId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                // Kullanıcı adını al ve kullanıcıyı ekle
                output.writeUTF("Enter your username: ");
                String username = input.readUTF();

                if (DatabaseManager.addUser(username)) {
                    userId = DatabaseManager.getUserId(username);
                    DatabaseManager.updateUserStatus(userId, true);
                    output.writeUTF("Welcome, " + username + "!");
                } else {
                    output.writeUTF("Username already exists. Try again.");
                    socket.close();
                    return;
                }

                // Mesaj dinleme döngüsü
                String message;
                while ((message = input.readUTF()) != null) {
                    System.out.println("Message from user " + userId + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("User disconnected: " + userId);
            } finally {
                DatabaseManager.updateUserStatus(userId, false);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
