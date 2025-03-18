package com.example.userservice.service.impl;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ Create a new user
    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        // No password encoding
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    // ✅ Get user by ID
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    // ✅ Get user by email (ensure consistent naming)
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    // ✅ Get all users
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ Update user (Ensure password is not encoded)
    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        // No password encoding (if password is provided, save as is)
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }

        return userRepository.save(user);
    }

    // ✅ Delete user by ID
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // ✅ Check if email already exists
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
