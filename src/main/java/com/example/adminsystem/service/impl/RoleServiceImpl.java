package com.example.adminsystem.service.impl;

import com.example.adminsystem.entity.Role;
import com.example.adminsystem.repository.RoleRepository;
import com.example.adminsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Page<Role> findAll(Pageable pageable) {
        if (pageable == null) {
            return Page.empty();
        }
        return roleRepository.findAll(pageable);
    }

    @Override
    public Role findById(Long id) {
        if (id == null) {
            return null;
        }
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    @SuppressWarnings("null")
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            roleRepository.deleteById(id);
        }
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return roleRepository.findAllById(ids);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}