package com.example.mindful_mentor.dto;

import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.Role;

public class UserLoginResponse {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String studentNumber;
    private Role role;
    private String token;
    private AccountStatus status;

    public UserLoginResponse(String id, String firstName, String middleName, String lastName,
                        String email, String phoneNumber, String studentNumber, Role role, String token, AccountStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.studentNumber = studentNumber;
        this.role = role;
        this.token = token;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public Role getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
