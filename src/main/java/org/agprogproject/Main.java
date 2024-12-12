package org.agprogproject;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Sunucuyu Başlat");
        System.out.println("2. İstemciyi Başlat");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            Server.startServer();
        } else if (choice == 2) {
            System.out.print("Kullanıcı adı: ");
            String username = scanner.nextLine();
            System.out.print("Şifre: ");
            String password = scanner.nextLine();
            Client.startClient(username, password);
        } else {
            System.out.println("Hatalı seçim.");
        }
    }
}
