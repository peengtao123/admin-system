package com.example.adminsystem.service;

import com.example.adminsystem.entity.Dictionary;
import com.example.adminsystem.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DictionaryService {
    @Autowired
    private DictionaryRepository dictionaryRepository;
    
    public List<Dictionary> findAll() {
        return dictionaryRepository.findAll();
    }
    
    public Optional<Dictionary> findById(@NonNull Long id) {
        return dictionaryRepository.findById(id);
    }
    
    public List<Dictionary> findByType(String type) {
        return dictionaryRepository.findByType(type);
    }
    
    public List<Dictionary> findByTypeAndStatus(String type, String status) {
        return dictionaryRepository.findByTypeAndStatus(type, status);
    }
    
    public Dictionary save(@NonNull Dictionary dictionary) {
        return dictionaryRepository.save(dictionary);
    }
    
    public void deleteById(@NonNull Long id) {
        dictionaryRepository.deleteById(id);
    }
}