package com.example.mindful_mentor.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class MoodDetail {
    
    private String code;
    private String description;

    // Default constructor
    public MoodDetail() {}

    // Constructor
    public MoodDetail(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
