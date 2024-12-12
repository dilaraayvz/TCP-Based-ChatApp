package org.agprogproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static final ConcurrentHashMap<String, Socket> connectedUsers = new ConcurrentHashMap<>();

    // Sunucuyu başlatma
    public static void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server is running on port " + PORT);

                // İstemcilerden gelen bağlantıları dinler
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Kullanıcı ekleme
    public static void addUser(String username, Socket socket) {
        connectedUsers.put(username, socket);
        System.out.println("User " + username + " has been added to the connected users list.");
    }

    // Kullanıcı kaldırma
    public static void removeUser(String username) {
        connectedUsers.remove(username);
        System.out.println("User " + username + " has been removed from the connected users list.");
    }

    // Kullanıcı soketi alma
    public static Socket getUserSocket(String username) {
        return connectedUsers.get(username);
    }

    // Mesaj gönderme
    public static void sendMessageToUser(String sender, String receiver, String message) {
        Socket receiverSocket = connectedUsers.get(receiver);

        if (receiverSocket != null) { // Alıcı çevrimiçi
            try {
                var output = new java.io.DataOutputStream(receiverSocket.getOutputStream());
                output.writeUTF("Message from " + sender + ": " + message);
                System.out.println("Message delivered to " + receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // Alıcı çevrimdışı
            System.out.println("User " + receiver + " is offline. Saving message.");
            DatabaseManager.saveMessage(sender, receiver, message);
        }
    }
}
