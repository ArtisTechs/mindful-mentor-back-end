package com.example.mindful_mentor.service;

import com.example.mindful_mentor.dto.UserSignupRequest;
import com.example.mindful_mentor.dto.UserLoginRequest;
import com.example.mindful_mentor.dto.UserLoginResponse;
import com.example.mindful_mentor.exception.DuplicateEmailException;
import com.example.mindful_mentor.exception.DuplicateStudentNumberException;
import com.example.mindful_mentor.exception.UserNotFoundException;
import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.User;
import com.example.mindful_mentor.repository.UserRepository;
import com.example.mindful_mentor.security.JwtUtil; // Import for JWT token generation

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
}
