package org.agprogproject;

import java.sql.*;

public class DatabaseManager {

    private static Connection connection;

    // Veritabanına bağlanma
    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:messaging_app.db");
            System.out.println("Connected to database.");

            // Tabloları oluştur
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Tabloları oluşturma
    private static void createTables() {
        try (Statement stmt = connection.createStatement()) {
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    socket_address TEXT,
                    is_online BOOLEAN DEFAULT FALSE
                );
            """;
            stmt.execute(createUsersTable);

            String createMessagesTable = """
                CREATE TABLE IF NOT EXISTS messages (
                    message_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sender_id INTEGER,
                    receiver_id INTEGER,
                    group_id INTEGER,
                    message_hash TEXT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (sender_id) REFERENCES users(user_id),
                    FOREIGN KEY (receiver_id) REFERENCES users(user_id),
                    FOREIGN KEY (group_id) REFERENCES groups(group_id)
                );
            """;
            stmt.execute(createMessagesTable);

            String createGroupsTable = """
                CREATE TABLE IF NOT EXISTS groups (
                    group_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    group_name TEXT NOT NULL UNIQUE
                );
            """;
            stmt.execute(createGroupsTable);

            String createGroupMembersTable = """
                CREATE TABLE IF NOT EXISTS group_members (
                    group_id INTEGER,
                    user_id INTEGER,
                    FOREIGN KEY (group_id) REFERENCES groups(group_id),
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    PRIMARY KEY (group_id, user_id)
                );
            """;
            stmt.execute(createGroupMembersTable);

            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcı ekleme
    public static boolean addUser(String username) {
        String query = "INSERT INTO users (username) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Username already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Kullanıcı ID'sini alma
    public static int getUserId(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Kullanıcı durumunu güncelleme
    public static void updateUserStatus(int userId, boolean isOnline) {
        String query = "UPDATE users SET is_online = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, isOnline);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Veritabanı bağlantısını kapatma
    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
