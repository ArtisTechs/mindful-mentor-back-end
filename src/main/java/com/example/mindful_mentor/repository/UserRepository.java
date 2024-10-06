package com.example.mindful_mentor.repository;

import com.example.mindful_mentor.model.User;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email); // Method to find user by email
    User findByStudentNumber(String email);
}
