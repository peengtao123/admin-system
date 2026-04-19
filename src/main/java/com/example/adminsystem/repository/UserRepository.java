package com.example.adminsystem.repository;

import com.example.adminsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    User findByUsernameWithRoles(String username);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> findAllWithRoles();
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    User findByIdWithRoles(Long id);
}