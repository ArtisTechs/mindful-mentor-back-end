package com.example.mindful_mentor.dto;

import com.example.mindful_mentor.model.AppointmentStatus;

public class AppointmentStatusUpdateDTO {
    private AppointmentStatus status;

    // Getter and Setter
    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
