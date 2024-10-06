package com.example.mindful_mentor.service;

import com.example.mindful_mentor.dto.UserSignupRequest;
import com.example.mindful_mentor.dto.UserLoginRequest;
import com.example.mindful_mentor.dto.UserLoginResponse;
import com.example.mindful_mentor.exception.DuplicateEmailException;
import com.example.mindful_mentor.exception.DuplicateStudentNumberException;
import com.example.mindful_mentor.exception.UserNotFoundException; 
import com.example.mindful_mentor.model.User;
import com.example.mindful_mentor.repository.UserRepository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        userRepository.save(user);
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
        
        // Create and return UserResponse with user details
        return new UserLoginResponse(
            user.getId().toString(), // Convert UUID to String if needed
            user.getFirstName(),
            user.getMiddleName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getStudentNumber(),
            user.getRole()
        );
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));
    }
}
