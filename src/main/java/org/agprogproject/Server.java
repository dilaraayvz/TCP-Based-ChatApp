package org.agprogproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 12345;
    private static final Map<String, ClientHandler> clientHandlers = new HashMap<>();

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Sunucu başlatıldı, port " + PORT + " üzerinde dinleniyor...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Bağlı kullanıcıyı kaydet
    public static void registerClientHandler(String username, ClientHandler handler) {
        synchronized (clientHandlers) {
            clientHandlers.put(username, handler);
        }
    }

    // Bağlı kullanıcıyı kaldır
    public static void unregisterClientHandler(String username) {
        synchronized (clientHandlers) {
            clientHandlers.remove(username);
        }
    }

    // Kullanıcıya özel ClientHandler al
    public static ClientHandler getClientHandler(String username) {
        synchronized (clientHandlers) {
            return clientHandlers.get(username);
        }
    }
}
