package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.model.UserRole;
import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    List<User> getUsersByRole(UserRole role);
    boolean existsByEmail(String email);
}
