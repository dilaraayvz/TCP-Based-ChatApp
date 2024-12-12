package org.agprogproject;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Kullanıcı giriş bilgilerini alın ve doğrulayın
            String username;
            String password;

            while (true) {
                username = in.readLine();
                password = in.readLine();

                if (DatabaseManager.isValidUser(username, password)) {
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
                    System.out.println(username + " bağlantıyı sonlandırdı.");
                    break;
                }

                // Mesajı tüm istemcilere veya hedeflenen kişilere yönlendirebilirsiniz
                // Bu kısımda mesajı işleme ve gönderme mantığını ekleyebilirsiniz
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
}
