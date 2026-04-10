package com.example.adminsystem.service;

import com.example.adminsystem.entity.Department;
import com.example.adminsystem.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Department save(Department department) {
        if (department != null) {
            return departmentRepository.save(department);
        }
        return null;
    }

    public void deleteById(Long id) {
        if (id != null) {
            departmentRepository.deleteById(id);
        }
    }

    public Department findById(Long id) {
        if (id != null) {
            return departmentRepository.findById(id).orElse(null);
        }
        return null;
    }
}