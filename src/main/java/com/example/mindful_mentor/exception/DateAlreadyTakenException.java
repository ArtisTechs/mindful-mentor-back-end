package com.example.mindful_mentor.exception;

public class DateAlreadyTakenException extends RuntimeException {
    public DateAlreadyTakenException(String message) {
        super(message);
    }
}
