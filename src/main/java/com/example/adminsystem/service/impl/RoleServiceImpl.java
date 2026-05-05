package com.example.adminsystem.service.impl;

import com.example.adminsystem.entity.Role;
import com.example.adminsystem.repository.RoleRepository;
import com.example.adminsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Role> findAll(Pageable pageable) {
        if (pageable == null) {
            return Page.empty();
        }
        return roleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(Long id) {
        if (id == null) {
            return null;
        }
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("null")
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id != null) {
            roleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return roleRepository.findAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}