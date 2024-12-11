package org.agprogproject;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void startClient() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            // Kullanıcı adı girişi
            System.out.print(input.readUTF()); // "Enter your username: "
            String username = scanner.nextLine();
            output.writeUTF(username);

            // Mesaj gönderme ve alma döngüsü
            new Thread(() -> {
                try {
                    while (true) {
                        String receivedMessage = input.readUTF();
                        System.out.println(receivedMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();
                output.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
