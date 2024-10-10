package com.example.mindful_mentor.dto;

import java.time.LocalDate;

public class AppointmentDateUpdateDTO {
    private LocalDate scheduledDate;

    // Getter and Setter
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}
