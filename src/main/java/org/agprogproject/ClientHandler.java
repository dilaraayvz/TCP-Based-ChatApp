package org.agprogproject;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private User user;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public User getUser() {
        return user;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Kullanıcı adı alınıyor
            out.println("Kullanıcı adınızı girin: ");
            String username = in.readLine();
            out.println("Sifrenizi girin: ");
            String password = in.readLine();
            this.user = new User(username, password,false);  // Başlangıçta offline

            System.out.println(username + " sunucuya bağlandı.");
            this.user.setOnline(true);  // Kullanıcı online oldu

            // Mesajları dinlemeye başlıyoruz
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println(user.getUsername() + ": " + message);

                // Mesajı sunucuya ilet
                Message msg = new Message(user, List.of(user), message);
                Server.sendMessageToAllUsers(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Kullanıcıyı offline yapıyoruz
                if (user != null) {
                    user.setOnline(false);
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Public olarak tanımlandı
    public void sendMessageToUser(User receiver, Message message) {
        if (this.user.equals(receiver)) {
            out.println("Yeni mesaj: " + message.getContent());
        }
    }
}
