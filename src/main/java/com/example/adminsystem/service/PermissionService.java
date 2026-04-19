package com.example.adminsystem.service;

import com.example.adminsystem.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;

public interface PermissionService {
    Page<Permission> findAll(Pageable pageable);
    @Nullable
    Permission findById(Long id);
    @Nullable
    Permission save(Permission permission);
    void deleteById(Long id);
    List<Permission> findByType(String type);
    List<Permission> findByParentId(Long parentId);
    List<Permission> findAll();
    List<Permission> findAllById(List<Long> ids);
}