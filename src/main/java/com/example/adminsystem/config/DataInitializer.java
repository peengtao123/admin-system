package com.example.adminsystem.config;

import com.example.adminsystem.entity.Department;
import com.example.adminsystem.entity.Role;
import com.example.adminsystem.entity.User;
import com.example.adminsystem.service.DepartmentService;
import com.example.adminsystem.service.RoleService;
import com.example.adminsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        // 创建默认部门
        if (departmentService.findAll().isEmpty()) {
            Department dept1 = new Department();
            dept1.setName("技术部");
            departmentService.save(dept1);

            Department dept2 = new Department();
            dept2.setName("人事部");
            departmentService.save(dept2);

            Department dept3 = new Department();
            dept3.setName("财务部");
            departmentService.save(dept3);
        }

        // 创建默认角色
        if (roleService.findAll().isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("超级管理员");
            roleService.save(adminRole);

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("普通用户");
            roleService.save(userRole);
        }

        // 创建默认管理员用户
        if (userService.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setDepartmentId(1L);
            List<Role> roles = new ArrayList<>();
            roles.add(roleService.findByName("ADMIN"));
            admin.setRoles(roles);
            userService.save(admin);
        }
    }
}