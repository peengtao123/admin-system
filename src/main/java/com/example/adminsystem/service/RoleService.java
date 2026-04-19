package com.example.adminsystem.service;

import com.example.adminsystem.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;

public interface RoleService {
    Page<Role> findAll(Pageable pageable);
    @Nullable
    Role findById(Long id);
    @Nullable
    Role save(Role role);
    void deleteById(Long id);
    List<Role> findAll();
    List<Role> findAllById(List<Long> ids);
    @Nullable
    Role findByName(String name);
}