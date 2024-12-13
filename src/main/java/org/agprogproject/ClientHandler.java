package org.agprogproject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private User currentUser; // Mevcut kullanıcı bilgisi

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Kullanıcı giriş bilgilerini alın ve doğrulayın
            while (true) {
                String username = in.readLine();
                String password = in.readLine();

                if (DatabaseManager.isValidUser(username, password)) {
                    currentUser = DatabaseManager.getUser(username); // Mevcut kullanıcıyı yükle
                    Server.registerClientHandler(username, this);    // Kullanıcıyı server'a kaydet
                    DatabaseManager.updateUserStatus(username, true); // Kullanıcıyı çevrimiçi olarak işaretle
                    out.println("Giriş başarılı!");
                    break;
                } else {
                    out.println("Kullanıcı adı veya şifre yanlış. Tekrar deneyin.");
                }
            }

            // Mesajları alıp işleyin
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                System.out.println("Kullanıcıdan gelen mesaj: " + clientMessage);

                // "exit" mesajı ile bağlantı sonlandırılır
                if (clientMessage.equalsIgnoreCase("exit")) {
                    System.out.println(currentUser.getUsername() + " bağlantıyı sonlandırdı.");
                    break;
                }

                // Mesajı işle ve gönder
                processMessage(clientMessage);
            }

        } catch (IOException e) {
            System.out.println("Bağlantı sonlandırıldı: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Mesajı işle ve hem veritabanına kaydet hem de alıcılara gönder
    private void processMessage(String clientMessage) {
        try {
            // Mesaj formatı: [alıcı1, alıcı2]: [mesaj içeriği]
            String[] parts = clientMessage.split(":", 2);
            if (parts.length < 2) {
                out.println("Hatalı mesaj formatı. Doğru format: [alıcı]: [mesaj]");
                return;
            }

            String receiversPart = parts[0].trim(); // Alıcılar
            String messageContent = parts[1].trim(); // Mesaj içeriği

            // Alıcıları listeye dönüştür
            List<User> receivers = new ArrayList<>();
            boolean invalidReceiverFound = false;

            if (receiversPart.equalsIgnoreCase("all")) {
                // Tüm kullanıcılara gönder
                receivers = DatabaseManager.getAllOnlineUsers(currentUser.getUsername());
            } else {
                String[] receiverUsernames = receiversPart.split(",");
                for (String receiverUsername : receiverUsernames) {
                    User user = DatabaseManager.getUser(receiverUsername.trim());
                    if (user != null) {
                        receivers.add(user);
                    } else {
                        out.println("Alıcı bulunamadı: " + receiverUsername.trim());
                        invalidReceiverFound = true;
                    }
                }
            }

            // Eğer geçersiz alıcı varsa mesajı kaydetme ve işlemi sonlandır
            if (invalidReceiverFound || receivers.isEmpty()) {
                out.println("Bazı alıcılar bulunamadı veya alıcı listesi boş. Mesaj veritabanına kaydedilmedi.");
                return;
            }

            // Mesajı veritabanına kaydet
            long timestamp = System.currentTimeMillis();
            String hash = generateHash(messageContent); // Hash oluştur
            if (!receivers.isEmpty()) {
                DatabaseManager.addMessage(currentUser, receivers, messageContent, timestamp, hash);
            } else {
                out.println("Alıcı listesi boş. Mesaj kaydedilmedi.");
            }

            // Mesajı alıcılara gönder
            for (User receiver : receivers) {
                if (receiver.isOnline()) {
                    ClientHandler receiverHandler = Server.getClientHandler(receiver.getUsername());
                    if (receiverHandler != null) {
                        receiverHandler.sendMessage("[" + currentUser.getUsername() + "]: " + messageContent);
                    }
                } else {
                    out.println("Alıcı çevrimdışı: " + receiver.getUsername() + ". Mesaj kaydedildi.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Mesaj gönderilirken bir hata oluştu.");
        }
    }

    // Mesaj gönderme
    public void sendMessage(String message) {
        out.println(message);
    }

    // Mesaj hash'i oluşturma
    private String generateHash(String content) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(content.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
