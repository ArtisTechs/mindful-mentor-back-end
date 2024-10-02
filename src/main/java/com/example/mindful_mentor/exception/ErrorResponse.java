package com.example.mindful_mentor.exception;

public class ErrorResponse {
    private int status;
    private String errorCode;  // Added error code
    private String message;

    public ErrorResponse(int status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode; // Initialize error code
        this.message = message;
    }

    public ErrorResponse(int value, ErrorCodes userNotFound) {
		// TODO Auto-generated constructor stub
	}

	// Getters and setters
    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode; // Getter for error code
    }

    public String getMessage() {
        return message;
    }
}
