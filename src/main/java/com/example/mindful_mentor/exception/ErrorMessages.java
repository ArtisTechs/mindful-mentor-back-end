package com.example.mindful_mentor.exception;

public enum ErrorMessages {
    EMAIL_NOT_REGISTERED("Email not registered."),
    EMAIL_ALREADY_REGISTERED("Email already exists."),
    STUDENT_NUMBER_ALREADY_REGISTERED("Student number already exists."),
    WRONG_PASSWORD("Incorrect password.");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
