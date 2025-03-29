package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.security.JwtTokenProvider;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // ✅ User Login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.getUserByEmail(loginRequest.getEmail());
            
            // Debugging: Log user details
            if (user != null) {
                System.out.println("User found in DB: " + user.getEmail());
            } else {
                System.out.println("No user found with email: " + loginRequest.getEmail());
            }
    
            // Check if user exists and if password matches (no encoding needed)
            if (user == null || !loginRequest.getPassword().equals(user.getPassword())) {
                System.out.println("Password mismatch or user not found.");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
            }
    
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
    
            // Set authentication context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
    
            // Create response with token and user info
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", user);
    
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            System.out.println("Authentication failed: " + ex.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }
    
    

    // ✅ User Registration (Without Roles)
    @PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestBody User user) {
    // Check if user already exists
    if (userService.existsByEmail(user.getEmail())) {
        return ResponseEntity.badRequest().body(Map.of("error", "Email is already taken"));
    }

    // Do not encode the password, save the plain text password
    user.setPassword(user.getPassword());

    // Save the user without any roles
    User savedUser = userService.createUser(user);
    return ResponseEntity.ok(savedUser);
}
}

// ✅ DTO for login request
class LoginRequest {
    private String email;
    private String password;

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
