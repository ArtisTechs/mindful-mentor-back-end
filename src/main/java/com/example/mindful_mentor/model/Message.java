package com.example.mindful_mentor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @NotNull
    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @NotEmpty
    @Column(nullable = false, length = 255)  // Add length constraint here
    private String content;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @NotNull
    @Column(name = "chat_token", nullable = false)
    private UUID chatToken;

    public Message() {
        // Default constructor - initialize timestamp only if needed
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // Optional: Constructor for easier instantiation
    public Message(UUID senderId, UUID receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public UUID getChatToken() {
        return chatToken;
    }

    public void setChatToken(UUID chatToken) {
        this.chatToken = chatToken;
    }
}
