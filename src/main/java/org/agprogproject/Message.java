package org.agprogproject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Message {
    private User sender;
    private List<User> receivers;  // Alıcılar bir liste olarak tutuluyor
    private String content;
    private long timestamp;
    private String hash;

    public Message(User sender, List<User> receivers, String content) {
        this.sender = sender;
        this.receivers = receivers;
        this.content = content;
        this.timestamp = System.currentTimeMillis();  // Mesajın gönderildiği zamanı alıyoruz
        this.hash = generateHash(content);  // Mesajın hash'ini oluşturuyoruz
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

    @Override
    public String toString() {
        StringBuilder receiversList = new StringBuilder();
        for (User receiver : receivers) {
            receiversList.append(receiver.getUsername()).append(", ");
        }
        return "Message{" +
                "sender=" + sender.getUsername() +
                ", receivers=" + receiversList.toString() +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", hash='" + hash + '\'' +
                '}';
    }
}
