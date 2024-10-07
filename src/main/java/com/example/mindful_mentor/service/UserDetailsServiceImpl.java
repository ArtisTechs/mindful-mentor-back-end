package com.example.mindful_mentor.service;

import com.example.mindful_mentor.model.AccountStatus;
import com.example.mindful_mentor.model.User; // Import your User model
import com.example.mindful_mentor.repository.UserRepository; // Import your User repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from the repository using email
        User user = userRepository.findByEmail(email); // Adjust method as per your UserRepository

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Check if the user's account status is active
        if (user.getStatus() != AccountStatus.ACTIVE) { // Ensure AccountStatus is the correct enum
            throw new UsernameNotFoundException("Account is not active for user: " + email);
        }

        // Convert User to UserDetails
        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        userBuilder.password(user.getPassword());
        userBuilder.roles(user.getRole().name()); // Assuming role is an enum and you want the role name

        return userBuilder.build();
    }
}
