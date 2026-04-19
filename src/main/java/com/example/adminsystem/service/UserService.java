package com.example.adminsystem.service;

import com.example.adminsystem.entity.User;
import com.example.adminsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsernameWithRoles(username);
    }

    public List<User> findAll() {
        return userRepository.findAllWithRoles();
    }

    public User save(User user) {
        if (user != null && user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteById(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    public User findById(Long id) {
        if (id != null) {
            return userRepository.findByIdWithRoles(id);
        }
        return null;
    }
}