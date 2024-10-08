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
import com.example.mindful_mentor.response.SuccessResponse;
import com.example.mindful_mentor.service.UserService;
import com.example.mindful_mentor.security.JwtUtil; // Import JWT utility

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil; // Autowire the JWT utility

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        Page<User> usersPage = userService.getAllUsers(status, role, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(usersPage);
    }
    
    @PutMapping("/profile/{id}")
    public ResponseEntity<SuccessResponse<String>> updateUserProfile(
            @PathVariable UUID id,
            @RequestBody UserProfileUpdateRequest updateRequest) {
        userService.updateUserProfile(id, updateRequest);
        SuccessResponse<String> successResponse = new SuccessResponse<>("User profile updated successfully.", "Profile update");
        return ResponseEntity.ok(successResponse);
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
