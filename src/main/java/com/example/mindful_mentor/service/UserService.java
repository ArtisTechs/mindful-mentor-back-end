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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private JwtUtil jwtUtil; // Autowire the JwtUtil class for token generation
    
    private static final String UPLOAD_DIR = "/path/to/uploads/";

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
        
        if (user.getProfilePicture() != null) {
            // Ensure the profile picture URL is set properly
            String profilePictureUrl = user.getProfilePicture();
            user.setProfilePicture(profilePictureUrl);  // If necessary, you can format the URL here
        }
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
    
    public Page<User> getAllUsers(String status, String role, String searchName, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDirection), sortBy);
        Page<User> usersPage;

        // Apply filters for status, role, and name
        if (searchName != null && !searchName.isEmpty()) {
            usersPage = userRepository.findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchName, searchName, searchName, pageable);
        } else if (status != null && !status.isEmpty() && role != null && !role.isEmpty()) {
            usersPage = userRepository.findByStatusAndRole(AccountStatus.valueOf(status.toUpperCase()), Role.valueOf(role.toUpperCase()), pageable);
        } else if (status != null && !status.isEmpty()) {
            usersPage = userRepository.findByStatus(AccountStatus.valueOf(status.toUpperCase()), pageable);
        } else if (role != null && !role.isEmpty()) {
            usersPage = userRepository.findByRole(Role.valueOf(role.toUpperCase()), pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        // Nullify the password for all users in the result set
        List<User> usersWithoutPassword = usersPage.getContent().stream()
                .peek(user -> user.setPassword(null)) // Set password to null
                .collect(Collectors.toList());

        // Return a new Page object with users who have password set to null
        return new PageImpl<>(usersWithoutPassword, pageable, usersPage.getTotalElements());
    }

    
    
    public void updateUserProfile(UUID userId, UserProfileUpdateRequest updateRequest) throws Exception {
        // Fetch the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Update other user fields
        user.setFirstName(updateRequest.getFirstName());
        user.setMiddleName(updateRequest.getMiddleName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        user.setStudentNumber(updateRequest.getStudentNumber());

        // Handle password update if provided
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        // Handle profile picture upload (saving the GitHub photo link)
        MultipartFile profilePicture = updateRequest.getProfilePicture();
        if (profilePicture != null && !profilePicture.isEmpty()) {
            String fileName = StringUtils.cleanPath(profilePicture.getOriginalFilename());

            // Check the content type of the uploaded file to ensure it's an image
            String contentType = profilePicture.getContentType();
            if (contentType != null && !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid file type. Only image files are allowed.");
            }

            try {
                // Upload the image to GitHub and get the image URL
                String imageUrl = gitHubService.uploadImage(profilePicture, fileName);

                // Set the profile picture URL (from GitHub) in the user object
                user.setProfilePicture(imageUrl);  // Save the GitHub image URL to the user entity

            } catch (IOException e) {
                throw new RuntimeException("Failed to store profile picture", e);
            }
        }

        // Save the updated user entity
        userRepository.save(user);
    }



}
