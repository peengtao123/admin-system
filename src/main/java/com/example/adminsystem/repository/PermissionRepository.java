package com.example.adminsystem.repository;

import com.example.adminsystem.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByType(String type);
    List<Permission> findByParentId(Long parentId);
}