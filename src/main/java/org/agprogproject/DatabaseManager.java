package org.agprogproject;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;


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

    // Kullanıcıyı getirme (username'e göre)
    public static User getUser(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBoolean("is_online")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Kullanıcı bulunamazsa null döner
    }

    // Mesaj ekleme
    // Mesaj ekleme
    public static void addMessage(User sender, List<User> receivers, String content, long timestamp, String hash) {
        String query = "INSERT INTO messages (sender, receivers, content, timestamp, hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            Gson gson = new Gson();
            String receiversJson = gson.toJson(receivers.stream().map(User::getUsername).toList()); // Kullanıcı adlarını JSON'a çevir

            stmt.setString(1, sender.getUsername());
            stmt.setString(2, receiversJson); // JSON olarak sakla
            stmt.setString(3, content);
            stmt.setLong(4, timestamp);
            stmt.setString(5, hash);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Message getMessage(int messageId) {
        String query = "SELECT * FROM messages WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User sender = getUser(rs.getString("sender"));
                String receiversJson = rs.getString("receivers");

                // JSON'dan listeye dönüştür
                Gson gson = new Gson();
                List<String> receiverUsernames = Arrays.asList(gson.fromJson(receiversJson, String[].class));
                List<User> receivers = new ArrayList<>();
                for (String username : receiverUsernames) {
                    User user = getUser(username);
                    if (user != null) {
                        receivers.add(user);
                    }
                }

                return new Message(
                        sender,
                        receivers,
                        rs.getString("content"),
                        rs.getLong("timestamp"),
                        rs.getString("hash")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Mesaj bulunamazsa null döner
    }


    // Yardımcı metod: Alıcıları string'e çevirme
    private static String convertReceiversToString(List<User> receivers) {
        StringBuilder sb = new StringBuilder();
        for (User user : receivers) {
            sb.append(user.getUsername()).append(",");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // Son virgülü kaldır
        }
        return sb.toString();
    }

    // Yardımcı metod: Alıcıları string'den listeye çevirme
    private static List<User> convertStringToReceivers(String receivers) {
        List<User> users = new ArrayList<>();
        String[] receiverArray = receivers.split(",");
        for (String username : receiverArray) {
            User user = getUser(username.trim());
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    public static List<User> getAllOnlineUsers(String excludeUsername) {
        List<User> onlineUsers = new ArrayList<>();
        String query = "SELECT * FROM users WHERE is_online = 1 AND username != ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, excludeUsername);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                onlineUsers.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBoolean("is_online")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return onlineUsers;
    }
}
