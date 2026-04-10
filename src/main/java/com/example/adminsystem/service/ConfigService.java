package com.example.adminsystem.service;

import com.example.adminsystem.entity.Config;
import com.example.adminsystem.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {
    @Autowired
    private ConfigRepository configRepository;
    
    public List<Config> findAll() {
        return configRepository.findAll();
    }
    
    public Optional<Config> findById(@NonNull Long id) {
        return configRepository.findById(id);
    }
    
    public Config findByConfigKey(String configKey) {
        return configRepository.findByConfigKey(configKey).orElse(null);
    }
    
    public Config save(@NonNull Config config) {
        return configRepository.save(config);
    }
    
    public void deleteById(@NonNull Long id) {
        configRepository.deleteById(id);
    }
    
    public String getConfigValue(String configKey, String defaultValue) {
        Config config = findByConfigKey(configKey);
        return config != null ? config.getConfigValue() : defaultValue;
    }
}