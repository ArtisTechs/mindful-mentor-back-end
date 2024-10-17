package com.example.mindful_mentor.controller;

import com.example.mindful_mentor.dto.UserLoginRequest;
import com.example.mindful_mentor.dto.UserLoginResponse;
import com.example.mindful_mentor.dto.UserProfileUpdateRequest;
import com.example.mindful_mentor.dto.UserSignupRequest;
import com.example.mindful_mentor.exception.DuplicateEmailException;
import com.example.mindful_mentor.exception.DuplicateStudentNumberException;
import com.example.mindful_mentor.exception.ErrorCodes;
import com.example.mindful_mentor.exception.ErrorMessages;
import com.example.mindful_mentor.exception.ErrorResponse;
import com.example.mindful_mentor.exception.UserNotFoundException;
import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.User;
import com.example.mindful_mentor.repository.UserRepository;
import com.example.mindful_mentor.response.SuccessResponse;
import com.example.mindful_mentor.service.FileService;
import com.example.mindful_mentor.service.GitHubService;
import com.example.mindful_mentor.service.UserService;
import com.example.mindful_mentor.websocket.WebSocketEventListener;
import com.example.mindful_mentor.security.JwtUtil; // Import JWT utility

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private JwtUtil jwtUtil; // Autowire the JWT utility
    
    @Autowired
    private WebSocketEventListener webSocketEventListener; 

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<String>> signUp(@RequestBody UserSignupRequest signupRequest) {
        userService.signUp(signupRequest);
        SuccessResponse<String> successResponse = new SuccessResponse<>("User registered successfully.", "User registration");
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<UserLoginResponse>> login(@RequestBody UserLoginRequest loginRequest) {
        // Ensure that loginRequest contains an email and password
        UserLoginResponse user = userService.login(loginRequest);

        // Check if the user's account status is ACTIVE
        if (user.getStatus() != AccountStatus.ACTIVE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse<>(null, ErrorMessages.USER_NOT_ACTIVE.getMessage()));
        }

        String token = jwtUtil.generateToken(user.getEmail()); // Generate the JWT token
        logger.info("User {} logged in successfully.", user.getEmail());

        // Include token in response
        SuccessResponse<UserLoginResponse> successResponse = new SuccessResponse<>(user, "User login successful.");
        successResponse.setToken(token);  // Add token to the success response
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getUserProfileById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user); // Return the user details without the password
    }
    
    @GetMapping("/list")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String searchName, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        Page<User> usersPage = userService.getAllUsers(status, role, searchName, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(usersPage);
    }

    
    @PutMapping("/profile/{id}")
    public ResponseEntity<String> updateUserProfile(
            @PathVariable UUID id,
            @RequestParam("firstName") String firstName,
            @RequestParam("middleName") String middleName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("studentNumber") String studentNumber,
            @RequestPart(value = "file", required = false) MultipartFile file) { // Handle file upload

        // Map the individual parameters to the updateRequest object
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setFirstName(firstName);
        updateRequest.setMiddleName(middleName);
        updateRequest.setLastName(lastName);
        updateRequest.setEmail(email);
        updateRequest.setPassword(password);
        updateRequest.setPhoneNumber(phoneNumber);
        updateRequest.setStudentNumber(studentNumber);

        // Handle file upload if present
        if (file != null && !file.isEmpty()) {
            try {
                // Upload image to GitHub and get the URL
                String imageName = file.getOriginalFilename(); // Use the original file name
                String imageUrl = gitHubService.uploadImage(file, imageName);

                // Instead of setting the image URL, we set the MultipartFile itself or the image URL
                updateRequest.setProfilePicture(file);  // Store the MultipartFile or you can keep the URL if necessary

            } catch (Exception e) {
                // Return an error response if the image upload fails
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Image upload failed: " + e.getMessage());
            }
        }

        // Call the user service to update the user profile with the updateRequest
        try {
            userService.updateUserProfile(id, updateRequest); // Pass the updated request with profile picture file
        } catch (Exception e) {
            // Handle user update failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User profile update failed: " + e.getMessage());
        }

        return ResponseEntity.ok("User profile updated successfully.");
    }




    @PostMapping("/status")
    public ResponseEntity<SuccessResponse<String>> changeUserStatus(@RequestParam UUID id, @RequestParam String status) {
        // Use the service to change the user status
        userService.updateUserStatus(id, status);
        SuccessResponse<String> successResponse = new SuccessResponse<>("User status updated successfully.", "Status update");
        return ResponseEntity.ok(successResponse);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponse<String>> deleteUser(@PathVariable UUID id) {
        // Use the service to delete the user
        userService.deleteUser(id);
        SuccessResponse<String> successResponse = new SuccessResponse<>("User deleted successfully.", "User deletion");
        return ResponseEntity.ok(successResponse);
    }


    // Exception handlers for various error types
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ErrorCodes.EMAIL_NOT_REGISTERED.getCode(), ErrorMessages.EMAIL_NOT_REGISTERED.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ErrorCodes.WRONG_PASSWORD.getCode(), ErrorMessages.WRONG_PASSWORD.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), ErrorCodes.EMAIL_ALREADY_REGISTERED.getCode(), ErrorMessages.EMAIL_ALREADY_REGISTERED.getMessage()));
    }

    @ExceptionHandler(DuplicateStudentNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateStudentNumberException(DuplicateStudentNumberException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT.value(), ErrorCodes.STUDENT_NUMBER_ALREADY_REGISTERED.getCode(), ErrorMessages.STUDENT_NUMBER_ALREADY_REGISTERED.getMessage()));
    }
}
