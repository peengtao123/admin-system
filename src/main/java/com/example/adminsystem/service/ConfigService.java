package com.example.adminsystem.service;

import com.example.adminsystem.entity.Config;
import com.example.adminsystem.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {
    @Autowired
    private ConfigRepository configRepository;
    
    @Transactional(readOnly = true)
    public List<Config> findAll() {
        return configRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Config> findById(@NonNull Long id) {
        return configRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Config findByConfigKey(String configKey) {
        return configRepository.findByConfigKey(configKey).orElse(null);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Config save(@NonNull Config config) {
        return configRepository.save(config);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(@NonNull Long id) {
        configRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public String getConfigValue(String configKey, String defaultValue) {
        Config config = findByConfigKey(configKey);
        return config != null ? config.getConfigValue() : defaultValue;
    }
}