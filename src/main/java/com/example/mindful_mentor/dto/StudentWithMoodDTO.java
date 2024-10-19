package com.example.mindful_mentor.dto;

import java.time.LocalDate;
import java.util.UUID;

public class StudentWithMoodDTO {

    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String studentNumber;
    private LocalDate moodDate;
    private String moodCode;
    private String moodDescription;
    private String profilePicture;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public LocalDate getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(LocalDate moodDate) {
        this.moodDate = moodDate;
    }

    public String getMoodCode() {
        return moodCode;
    }

    public void setMoodCode(String moodCode) {
        this.moodCode = moodCode;
    }

    public String getMoodDescription() {
        return moodDescription;
    }

    public void setMoodDescription(String moodDescription) {
        this.moodDescription = moodDescription;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
