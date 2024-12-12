package org.agprogproject;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TCP-Based Chat Application Test ===");
        System.out.println("1. Start Server");
        System.out.println("2. Start Client");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Buffer temizliği

        switch (choice) {
            case 1 -> {
                // Sunucuyu başlat
                DatabaseManager.connect();
                Server.startServer();
            }
            case 2 -> {
                // İstemciyi başlat
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                Client.startClient(username, password);
            }
            default -> System.out.println("Invalid option. Exiting.");
        }
    }
}
