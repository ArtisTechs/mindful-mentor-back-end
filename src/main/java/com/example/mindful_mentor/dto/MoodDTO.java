package com.example.mindful_mentor.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.example.mindful_mentor.model.MoodDetail;

public class MoodDTO {
    private UUID userId; // User's UUID
    private LocalDate date; // Date of the mood entry
    private MoodDetail mood; // MoodDetail containing code and description

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MoodDetail getMood() {
        return mood;
    }

    public void setMood(MoodDetail mood) {
        this.mood = mood;
    }
}
