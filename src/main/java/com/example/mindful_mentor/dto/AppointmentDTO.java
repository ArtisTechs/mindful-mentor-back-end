package com.example.mindful_mentor.dto;

import java.time.LocalDate;
import java.util.UUID;

public class AppointmentDTO {
    private UUID userId;
    private LocalDate scheduledDate;

    // Getters and Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
