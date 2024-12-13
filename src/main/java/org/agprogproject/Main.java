package org.agprogproject;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean serverChoice = true;
        do {
            System.out.println("1. Sunucuyu Başlat");
            System.out.println("2. İstemciyi Başlat");
            int choice = scanner.nextInt();
            scanner.nextLine();
            serverChoice = true;
            if (choice == 1) {
                Server.startServer();
                serverChoice = true;
            } else if (choice == 2) {
                do {
                    System.out.print("Kullanıcı adı: ");
                    String username = scanner.nextLine();
                    System.out.print("Şifre: ");
                    String password = scanner.nextLine();
                    if (DatabaseManager.isValidUser(username, password)) {
                        Client.startClient(username, password);
                        serverChoice = true;
                    } else {
                        System.out.println("Hatalı giriş.");
                        serverChoice = false;
                    }
                } while (serverChoice == false);

            } else {
                System.out.println("Hatalı seçim.");
                serverChoice = false;
            }
        } while (serverChoice == false);

    }
}
