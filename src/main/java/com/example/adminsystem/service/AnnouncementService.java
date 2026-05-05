package com.example.adminsystem.service;

import com.example.adminsystem.entity.Announcement;
import com.example.adminsystem.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Transactional(readOnly = true)
    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Announcement> findById(@NonNull Long id) {
        return announcementRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Announcement> findByStatus(String status) {
        return announcementRepository.findByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Announcement> findByType(String type) {
        return announcementRepository.findByType(type);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Announcement save(@NonNull Announcement announcement) {
        Date now = new Date();
        if (announcement.getId() == null) {
            announcement.setCreatedAt(now);
        }
        announcement.setUpdatedAt(now);
        return announcementRepository.save(announcement);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(@NonNull Long id) {
        announcementRepository.deleteById(id);
    }
}