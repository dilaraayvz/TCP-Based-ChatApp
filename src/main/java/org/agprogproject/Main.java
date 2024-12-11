package org.agprogproject;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Sunucuyu otomatik başlat
        Server.startServer();

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TCP-Based Chat Application ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Buffer temizliği

        switch (choice) {
            case 1 -> {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                DatabaseManager.connect();
                if (DatabaseManager.registerUser(username, password)) {
                    System.out.println("User registered successfully!");
                } else {
                    System.out.println("Registration failed. Username might already exist.");
                }
                DatabaseManager.close();
            }
            case 2 -> {
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
