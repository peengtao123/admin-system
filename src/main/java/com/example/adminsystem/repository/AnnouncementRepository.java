package com.example.adminsystem.repository;

import com.example.adminsystem.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByStatus(String status);
    List<Announcement> findByType(String type);
}