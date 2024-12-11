package org.agprogproject;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TCP-Based Chat Application ===");
        System.out.println("1. Start Server");
        System.out.println("2. Start Client");
        System.out.println("3. Test Database");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Buffer temizliği

        switch (choice) {
            case 1 -> {
                System.out.println("Starting server...");
                Server.startServer(); // Server sınıfındaki startServer metodunu çağır
            }
            case 2 -> {
                System.out.println("Starting client...");
                Client.startClient(); // Client sınıfındaki startClient metodunu çağır
            }
            case 3 -> {
                System.out.println("Testing database...");
                DatabaseManager.connect(); // Veritabanına bağlan
                DatabaseManager.addUser("test_user"); // Kullanıcı ekle
                DatabaseManager.close(); // Bağlantıyı kapat
            }
            default -> System.out.println("Invalid option. Exiting.");
        }
    }
}
