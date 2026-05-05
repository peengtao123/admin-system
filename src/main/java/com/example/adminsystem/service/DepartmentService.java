package com.example.adminsystem.service;

import com.example.adminsystem.entity.Department;
import com.example.adminsystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Department save(Department department) {
        if (department != null) {
            return departmentRepository.save(department);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (id != null) {
            departmentRepository.deleteById(id);
        }
    }

    @Transactional(readOnly = true)
    public Department findById(Long id) {
        if (id != null) {
            return departmentRepository.findById(id).orElse(null);
        }
        return null;
    }
}