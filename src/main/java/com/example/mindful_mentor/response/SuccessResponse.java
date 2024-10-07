package com.example.mindful_mentor.response;

public class SuccessResponse<T> {
    private T data;
    private String message;
    private String token; // New field to store the token

    public SuccessResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    // Getters and Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // New token getter and setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
