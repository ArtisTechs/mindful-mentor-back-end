package com.example.mindful_mentor.repository;

import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.Role;
import com.example.mindful_mentor.model.User;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email); // Method to find user by email
    User findByStudentNumber(String email);
    Page<User> findByStatus(AccountStatus status, Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByStatusAndRole(AccountStatus status, Role role, Pageable pageable);
}
