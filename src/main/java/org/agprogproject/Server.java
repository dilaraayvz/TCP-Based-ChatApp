package org.agprogproject;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static final ConcurrentHashMap<String, Socket> connectedUsers = new ConcurrentHashMap<>();

    public static void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server is running on port " + PORT);

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

    public static void addUser(String username, Socket socket) {
        connectedUsers.put(username, socket);
    }

    public static void removeUser(String username) {
        connectedUsers.remove(username);
    }

    public static Socket getUserSocket(String username) {
        return connectedUsers.get(username);
    }

    public static void sendMessageToUser(String sender, String receiver, String message) {
        Socket receiverSocket = connectedUsers.get(receiver);
        if (receiverSocket != null) {
            try {
                DataOutputStream output = new DataOutputStream(receiverSocket.getOutputStream());
                output.writeUTF("Message from " + sender + ": " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User " + receiver + " is offline. Saving message.");
            DatabaseManager.saveMessage(sender, receiver, message);
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private DataInputStream input;
        private DataOutputStream output;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                // Kullanıcıdan username ve password alınır
                output.writeUTF("Enter your username: ");
                String username = input.readUTF();

                output.writeUTF("Enter your password: ");
                String password = input.readUTF();

                if (DatabaseManager.loginUser(username, password)) {
                    Server.addUser(username, socket);
                    output.writeUTF("Welcome, " + username + "!");

                    String message;
                    while ((message = input.readUTF()) != null) {
                        String[] parts = message.split(":", 2);
                        if (parts.length == 2) {
                            String receiver = parts[0];
                            String content = parts[1];
                            Server.sendMessageToUser(username, receiver, content);
                        } else {
                            output.writeUTF("Invalid message format. Use: receiver:message");
                        }
                    }
                } else {
                    output.writeUTF("Invalid username or password.");
                }
            } catch (IOException e) {
                System.out.println("Client disconnected.");
            } finally {
                Server.removeUser(socket.getInetAddress().toString());
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
