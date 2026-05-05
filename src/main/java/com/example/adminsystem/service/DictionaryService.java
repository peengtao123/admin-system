package com.example.adminsystem.service;

import com.example.adminsystem.entity.Dictionary;
import com.example.adminsystem.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DictionaryService {
    @Autowired
    private DictionaryRepository dictionaryRepository;
    
    @Transactional(readOnly = true)
    public List<Dictionary> findAll() {
        return dictionaryRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Dictionary> findById(@NonNull Long id) {
        return dictionaryRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Dictionary> findByType(String type) {
        return dictionaryRepository.findByType(type);
    }
    
    @Transactional(readOnly = true)
    public List<Dictionary> findByTypeAndStatus(String type, String status) {
        return dictionaryRepository.findByTypeAndStatus(type, status);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Dictionary save(@NonNull Dictionary dictionary) {
        return dictionaryRepository.save(dictionary);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(@NonNull Long id) {
        dictionaryRepository.deleteById(id);
    }
}