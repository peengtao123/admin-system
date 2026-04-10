package com.example.adminsystem.repository;

import com.example.adminsystem.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
    List<Dictionary> findByType(String type);
    List<Dictionary> findByTypeAndStatus(String type, String status);
}