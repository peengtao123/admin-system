package com.example.adminsystem.service;

import com.example.adminsystem.entity.User;
import com.example.adminsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsernameWithRoles(username);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllWithRoles();
    }

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) {
        if (user != null && user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        if (id != null) {
            return userRepository.findByIdWithRoles(id);
        }
        return null;
    }
}