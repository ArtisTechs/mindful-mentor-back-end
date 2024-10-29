package com.example.mindful_mentor.dto;

import java.util.Date;
import java.util.UUID;

public class JournalDTO {
    private UUID id;
    private Date entryDate; // Make sure this matches the Journal's entryDate type
    private String title;
    private String message;

    // No-argument constructor
    public JournalDTO() {}

    // Constructor
    public JournalDTO(UUID id, Date entryDate, String title, String message) {
        this.id = id;
        this.entryDate = entryDate;
        this.title = title;
        this.message = message;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
