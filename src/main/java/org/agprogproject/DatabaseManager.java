package org.agprogproject;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:messaging_app.db";

    // Veritabanına bağlantı sağlar
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            createTablesIfNotExists(connection); // Tabloları kontrol et ve oluştur
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Veritabanı tablolarını kontrol eder ve yoksa oluşturur
    private static void createTablesIfNotExists(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            // Users tablosu
            String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        is_online BOOLEAN DEFAULT 0
                    );
                    """;
            stmt.execute(createUsersTable);

            // Messages tablosu
            String createMessagesTable = """
                    CREATE TABLE IF NOT EXISTS messages (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        sender TEXT NOT NULL,
                        receivers TEXT NOT NULL,
                        content TEXT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        hash TEXT NOT NULL,
                        FOREIGN KEY (sender) REFERENCES users (username)
                    );
                    """;
            stmt.execute(createMessagesTable);

            //System.out.println("Tablolar başarıyla kontrol edildi veya oluşturuldu.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcı doğrulaması
    public static boolean isValidUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kullanıcı ekleme (Örnek veriler için kullanılabilir)
    public static void addUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mesaj ekleme
    public static void addMessage(String sender, String receivers, String content, long timestamp, String hash) {
        String query = "INSERT INTO messages (sender, receivers, content, timestamp, hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sender);
            stmt.setString(2, receivers);
            stmt.setString(3, content);
            stmt.setLong(4, timestamp);
            stmt.setString(5, hash);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
