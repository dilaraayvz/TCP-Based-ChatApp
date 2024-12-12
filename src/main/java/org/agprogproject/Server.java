package org.agprogproject;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>(); // Bağlantıdaki tüm clientları tutar

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Sunucu başlatıldı, port " + PORT + " üzerinde dinleniyor...");

            while (true) {
                // Yeni istemci bağlantısı kabul ediliyor
                Socket clientSocket = serverSocket.accept();
                System.out.println("Yeni istemci bağlandı: " + clientSocket.getInetAddress());

                // ClientHandler başlatılıyor ve istemci bağlantısı işleniyor
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sunucuya bağlı tüm kullanıcılara mesaj gönder
    public static void sendMessageToAllUsers(Message message) {
        for (ClientHandler handler : clientHandlers) {
            for (User receiver : message.getReceivers()) {
                handler.sendMessageToUser(receiver, message); // Mesajı alıcıya gönder
            }
        }
    }

    // Belirli bir kullanıcıya mesaj gönder
    public static void sendMessageToUser(User receiver, Message message) {
        for (ClientHandler handler : clientHandlers) {
            if (handler.getUser().equals(receiver)) {
                handler.sendMessageToUser(receiver, message); // Mesajı gönder
                break;
            }
        }
    }
}
