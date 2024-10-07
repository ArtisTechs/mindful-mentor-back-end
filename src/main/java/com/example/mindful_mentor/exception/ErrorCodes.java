package com.example.mindful_mentor.exception;

public enum ErrorCodes {
    EMAIL_NOT_REGISTERED("EMAIL_NOT_REGISTERED"),
    EMAIL_ALREADY_REGISTERED("EMAIL_ALREADY_REGISTERED"),
    STUDENT_NUMBER_ALREADY_REGISTERED("STUDENT_NUMBER_ALREADY_REGISTERED"),
    WRONG_PASSWORD("WRONG_PASSWORD"),
	USER_NOT_FOUND("USER_NOT_FOUND"),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
	USER_NOT_ACTIVE("USER_NOT_ACTIVE");

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
