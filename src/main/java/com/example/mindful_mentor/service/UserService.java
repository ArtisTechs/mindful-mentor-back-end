package com.example.mindful_mentor.service;

import com.example.mindful_mentor.dto.UserSignupRequest;
import com.example.mindful_mentor.dto.UserLoginRequest;
import com.example.mindful_mentor.dto.UserLoginResponse;
import com.example.mindful_mentor.dto.UserProfileUpdateRequest;
import com.example.mindful_mentor.exception.DuplicateEmailException;
import com.example.mindful_mentor.exception.DuplicateStudentNumberException;
import com.example.mindful_mentor.exception.UserNotFoundException;
import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.Role;
import com.example.mindful_mentor.model.User;
import com.example.mindful_mentor.repository.UserRepository;
import com.example.mindful_mentor.security.JwtUtil; // Import for JWT token generation

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; // Autowire the JwtUtil class for token generation

    public void signUp(UserSignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()) != null) {
            throw new DuplicateEmailException("Email is already registered.");
        }

        if (userRepository.findByStudentNumber(signupRequest.getStudentNumber()) != null) {
            throw new DuplicateStudentNumberException("Student number is already registered.");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName(signupRequest.getFirstName());
        user.setMiddleName(signupRequest.getMiddleName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPhoneNumber(signupRequest.getPhoneNumber());
        user.setStudentNumber(signupRequest.getStudentNumber());
        user.setRole(signupRequest.getRole());
        user.setStatus(AccountStatus.REGISTERED);

        userRepository.save(user);
        logger.info("User signed up: {}", signupRequest.getEmail());
    }

    public UserLoginResponse login(UserLoginRequest loginRequest) {
        // Check if the user exists by email
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            // If user not found, throw UserNotFoundException
            throw new UserNotFoundException("Email not registered."); 
        }
        
        // Verify the password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Wrong password provided."); // Throw exception for wrong password
        }
        
        // Generate the JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        logger.info("Generated token for user: {}", user.getEmail());

        // Create and return UserResponse with user details and token
        return new UserLoginResponse(
            user.getId().toString(), // Convert UUID to String if needed
            user.getFirstName(),
            user.getMiddleName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getStudentNumber(),
            user.getRole(),
            token,
            user.getStatus()
        );
    }

    public User getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
        
        // Set the password to null before returning the user
        user.setPassword(null);
        return user;
    }
    
    public void updateUserStatus(UUID userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        // Assuming AccountStatus is an enum
        user.setStatus(AccountStatus.valueOf(status.toUpperCase()));  // Converts string to enum
        userRepository.save(user);
    }
    
    public void deleteUser(UUID userId) {
        // Check if the user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        // Delete the user
        userRepository.delete(user);
    }
    
    public Page<User> getAllUsers(String status, String role, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        Page<User> usersPage;
        
        // Logic for filtering users by status and role
        if (status != null && !status.isEmpty() && role != null && !role.isEmpty()) {
            // If both status and role filters are provided
            usersPage = userRepository.findByStatusAndRole(AccountStatus.valueOf(status.toUpperCase()), Role.valueOf(role.toUpperCase()), pageable);
        } else if (status != null && !status.isEmpty()) {
            // If only status filter is provided
            usersPage = userRepository.findByStatus(AccountStatus.valueOf(status.toUpperCase()), pageable);
        } else if (role != null && !role.isEmpty()) {
            // If only role filter is provided
            usersPage = userRepository.findByRole(Role.valueOf(role.toUpperCase()), pageable);
        } else {
            // If no filters are provided, fetch all users
            usersPage = userRepository.findAll(pageable);
        }

        // Nullify the password for all users in the result set
        List<User> usersWithoutPassword = usersPage.getContent().stream()
                .peek(user -> user.setPassword(null)) // Set password to null
                .collect(Collectors.toList());

        // Return a new Page object with users who have password set to null
        return new PageImpl<>(usersWithoutPassword, pageable, usersPage.getTotalElements());
    }
    
    public void updateUserProfile(UUID userId, UserProfileUpdateRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Update fields except role and status
        user.setFirstName(updateRequest.getFirstName());
        user.setMiddleName(updateRequest.getMiddleName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setStudentNumber(updateRequest.getStudentNumber());

        userRepository.save(user); // Save updated user
    }
}
