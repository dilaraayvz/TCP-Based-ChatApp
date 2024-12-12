package org.agprogproject;

import java.io.*;
import java.net.*;

public class Client {
    public static void startClient(String username, String password) {
        final String SERVER_ADDRESS = "localhost"; // Sunucu adresi
        final int SERVER_PORT = 12345; // Sunucu portu

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Kullanıcı adı ve şifre doğrulama kısmı (gereksinime göre eklenebilir)
            out.println(username);  // Sunucuya kullanıcı adı gönderilir
            out.println(password);  // Sunucuya şifre gönderilir

            // Mesajları almak için ayrı bir thread başlatılır
            new Thread(() -> receiveMessages(in)).start();

            // Mesaj gönderme döngüsü
            while (true) {
                System.out.print("Mesajınızı yazın (çıkmak için 'exit' yazın): ");
                String message = userInput.readLine();

                if (message.equalsIgnoreCase("exit")) {
                    out.println("exit");
                    break;
                }

                out.println(message);  // Sunucuya mesaj gönderilir
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sunucudan gelen mesajları ekrana yazdıran metod
    private static void receiveMessages(BufferedReader in) {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Yeni mesaj: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
