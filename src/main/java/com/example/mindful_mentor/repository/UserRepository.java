package com.example.mindful_mentor.repository;

import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.Role;
import com.example.mindful_mentor.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

// Extend JpaSpecificationExecutor for Specification support
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    User findByEmail(String email); // Method to find user by email
    User findByStudentNumber(String studentNumber); // Corrected argument name
    Page<User> findByStatus(AccountStatus status, Pageable pageable);
    Page<User> findByRole(Role role, Pageable pageable);
    Page<User> findByStatusAndRole(AccountStatus status, Role role, Pageable pageable);
    User findUserById(UUID id);
    Page<User> findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String middleName, String lastName, Pageable pageable);
}
