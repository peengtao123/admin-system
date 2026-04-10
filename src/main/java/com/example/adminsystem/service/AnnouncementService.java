package com.example.adminsystem.service;

import com.example.adminsystem.entity.Announcement;
import com.example.adminsystem.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }
    
    public Optional<Announcement> findById(@NonNull Long id) {
        return announcementRepository.findById(id);
    }
    
    public List<Announcement> findByStatus(String status) {
        return announcementRepository.findByStatus(status);
    }
    
    public List<Announcement> findByType(String type) {
        return announcementRepository.findByType(type);
    }
    
    public Announcement save(@NonNull Announcement announcement) {
        Date now = new Date();
        if (announcement.getId() == null) {
            announcement.setCreatedAt(now);
        }
        announcement.setUpdatedAt(now);
        return announcementRepository.save(announcement);
    }
    
    public void deleteById(@NonNull Long id) {
        announcementRepository.deleteById(id);
    }
}