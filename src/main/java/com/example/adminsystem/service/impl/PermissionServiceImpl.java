package com.example.adminsystem.service.impl;

import com.example.adminsystem.entity.Permission;
import com.example.adminsystem.repository.PermissionRepository;
import com.example.adminsystem.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Page<Permission> findAll(Pageable pageable) {
        if (pageable == null) {
            return Page.empty();
        }
        return permissionRepository.findAll(pageable);
    }

    @Override
    public Permission findById(Long id) {
        if (id == null) {
            return null;
        }
        return permissionRepository.findById(id).orElse(null);
    }

    @Override
    @SuppressWarnings("null")
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            permissionRepository.deleteById(id);
        }
    }

    @Override
    public List<Permission> findByType(String type) {
        return permissionRepository.findByType(type);
    }

    @Override
    public List<Permission> findByParentId(Long parentId) {
        return permissionRepository.findByParentId(parentId);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public List<Permission> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return permissionRepository.findAllById(ids);
    }
}