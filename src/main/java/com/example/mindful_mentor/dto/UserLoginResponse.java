package com.example.mindful_mentor.dto;

import com.example.mindful_mentor.model.Role;

public class UserLoginResponse {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String studentNumber;
    private Role role; // Assuming you have roles in your User model

    public UserLoginResponse(String id, String firstName, String middleName, String lastName,
                        String email, String phoneNumber, String studentNumber, Role string) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.studentNumber = studentNumber;
        this.role = string;
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
}
