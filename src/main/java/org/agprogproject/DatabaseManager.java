package org.agprogproject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:chatapp.db";  // Veritabanı dosyasının yolu

    // Veritabanı bağlantısı sağlanır
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Kullanıcıyı veritabanına ekler
    public static void addUser(User user) {
        String query = "INSERT INTO users (username, password, is_online) VALUES (?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());  // Şifreyi düz metin olarak alıyoruz
            stmt.setBoolean(3, user.isOnline());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Tüm kullanıcıları getirir
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");  // Şifreyi düz metin olarak alıyoruz
                boolean isOnline = rs.getBoolean("is_online");

                users.add(new User(username, password, isOnline));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
    // Kullanıcıyı veritabanından alır
    public static User getUser(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                boolean isOnline = rs.getBoolean("is_online");
                user = new User(username, password, isOnline);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    // Kullanıcıyı çevrimiçi yapar
    public static void setUserOnline(String username, boolean isOnline) {
        String query = "UPDATE users SET is_online = ? WHERE username = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isOnline);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mesajı veritabanına kaydeder
    public static void addMessage(Message message) {
        String query = "INSERT INTO messages (sender, receivers, content, timestamp, hash) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, message.getSender().getUsername());
            stmt.setString(2, getReceiversAsString(message.getReceivers()));  // Alıcıları string olarak kaydediyoruz
            stmt.setString(3, message.getContent());
            stmt.setLong(4, message.getTimestamp());
            stmt.setString(5, message.getHash());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mesajları alır
    public static List<Message> getMessages(String username) {
        String query = "SELECT * FROM messages WHERE receivers LIKE ? OR receivers = 'all'";
        List<Message> messages = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + username + "%");  // Kullanıcının alıcı olduğu mesajları alır
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String receivers = rs.getString("receivers");
                String content = rs.getString("content");
                long timestamp = rs.getLong("timestamp");
                String hash = rs.getString("hash");

                User senderUser = new User(sender, "", true);  // Şu an için password boş
                List<User> receiverList = getReceiversFromString(receivers);

                messages.add(new Message(senderUser, receiverList, content));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    // Alıcıları string formatından liste formatına dönüştürür
    private static List<User> getReceiversFromString(String receivers) {
        List<User> userList = new ArrayList<>();
        String[] usernames = receivers.split(",");

        for (String username : usernames) {
            userList.add(new User(username.trim(), "", true));  // Şu an için password boş
        }

        return userList;
    }

    // Alıcıları liste formatından string formatına dönüştürür
    private static String getReceiversAsString(List<User> receivers) {
        StringBuilder receiverString = new StringBuilder();

        for (User user : receivers) {
            receiverString.append(user.getUsername()).append(", ");
        }

        return receiverString.toString().isEmpty() ? "all" : receiverString.toString();  // Eğer alıcı yoksa, 'all' yazılır
    }
}
