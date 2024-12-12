package org.agprogproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static Connection connection;

    // Veritabanına bağlanma
    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:messaging_app.db");
            createTables();
            System.out.println("Database connected successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Veritabanı bağlantısını kapatma
    public static void close() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tabloları oluşturma
    private static void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    is_online BOOLEAN DEFAULT FALSE
                );
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    message_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender TEXT NOT NULL,
                    receiver TEXT NOT NULL,
                    content TEXT NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    is_delivered BOOLEAN DEFAULT FALSE
                );
            """);
        }
    }

    // Kullanıcı kaydı
    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password, is_online) VALUES (?, ?, false)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Username already exists: " + username);
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Kullanıcı giriş işlemi
    public static boolean loginUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); // Şifre doğrulama
            } else {
                System.out.println("Username not found: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kullanıcı çevrimiçi durumu güncelleme
    public static void setUserOnline(String username, boolean isOnline) {
        String query = "UPDATE users SET is_online = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, isOnline);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mesaj kaydetme
    public static void saveMessage(String sender, String receiver, String content) {
        String query = "INSERT INTO messages (sender, receiver, content, is_delivered) VALUES (?, ?, ?, false)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Teslim edilmemiş mesajları alma
    public static List<String> getUndeliveredMessages(String receiver) {
        String query = "SELECT sender, content FROM messages WHERE receiver = ? AND is_delivered = false";
        List<String> messages = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, receiver);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String content = rs.getString("content");
                messages.add("From " + sender + ": " + content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    // Teslim edilmiş olarak işaretleme
    public static void markMessagesAsDelivered(String receiver) {
        String query = "UPDATE messages SET is_delivered = true WHERE receiver = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, receiver);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
