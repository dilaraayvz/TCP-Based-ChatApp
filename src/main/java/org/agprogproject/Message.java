package org.agprogproject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Message {
    private User sender;
    private List<User> receivers;  // Alıcılar bir liste olarak tutulur
    private String content;
    private long timestamp;
    private String hash;

    public Message(User sender, List<User> receivers, String content,long timestamp,String hash) {
        this.sender = sender;
        this.receivers = receivers;
        this.content = content;
        this.timestamp = System.currentTimeMillis();  // Mesajın gönderim zamanı
        this.hash = generateHash(content);  // Mesajın hash'i
    }

    private String generateHash(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(content.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getSender() {
        return sender;
    }

    public List<User> getReceivers() {
        return receivers;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }
}
