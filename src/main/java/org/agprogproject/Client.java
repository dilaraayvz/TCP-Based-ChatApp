package org.agprogproject;

import java.io.*;
import java.net.Socket;

public class Client {

    public static void startClient(String username, String password) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Kullanıcı giriş bilgilerini gönder
            out.println(username);
            out.println(password);

            // Sunucudan yanıt beklenir
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
                // Giriş başarılıysa mesajlaşma başlasın
                if (response.equals("Giriş başarılı!")) {
                    System.out.println("Mesaj göndermeye başlayabilirsiniz.");
                    System.out.println("Formatlar:");
                    System.out.println("Bir kişiye mesaj: [alıcı]: [mesaj]");
                    System.out.println("Birden fazla kişiye mesaj: [alıcı1, alıcı2]: [mesaj]");
                    System.out.println("Herkese mesaj: all: [mesaj]");
                    break;
                }
            }

            // Mesaj gönderme ve alma döngüsü
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("Sunucudan gelen mesaj: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Kullanıcıdan mesaj al ve sunucuya gönder
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                if (userMessage.equalsIgnoreCase("exit")) {
                    System.out.println("Bağlantı sonlandırılıyor...");
                    break;
                }

                // Mesaj formatını kontrol et
                if (userMessage.matches("^.+: .+$")) {
                    out.println(userMessage);  // Mesajı sunucuya gönder
                } else {
                    System.out.println("Hatalı format. Doğru format: [alıcı]: [mesaj]");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
