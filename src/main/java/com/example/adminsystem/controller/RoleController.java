package com.example.adminsystem.controller;

import com.example.adminsystem.entity.Permission;
import com.example.adminsystem.entity.Role;
import com.example.adminsystem.service.PermissionService;
import com.example.adminsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page, 
                      @RequestParam(defaultValue = "10") int size, 
                      Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleService.findAll(pageable);
        model.addAttribute("roles", roles);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "role/list";
    }

    @GetMapping("/form")
    public String form(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("role", roleService.findById(id));
        } else {
            model.addAttribute("role", new Role());
        }
        return "role/form";
    }

    @PostMapping("/save")
    public String save(Role role) {
        roleService.save(role);
        return "redirect:/role/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        roleService.deleteById(id);
        return "redirect:/role/list";
    }
    
    @GetMapping("/permission/{id}")
    public String permission(@PathVariable("id") Long id, Model model) {
        Role role = roleService.findById(id);
        if (role == null) {
            return "redirect:/role/list";
        }
        List<Permission> permissions = permissionService.findAll();
        model.addAttribute("role", role);
        model.addAttribute("permissions", permissions);
        return "role/permission";
    }
    
    @PostMapping("/permission/save")
    public String savePermission(@RequestParam Long roleId, @RequestParam(value = "permissionIds", required = false) List<Long> permissionIds) {
        Role role = roleService.findById(roleId);
        if (role != null) {
            List<Permission> permissions = permissionService.findAllById(permissionIds);
            role.setPermissions(permissions);
            roleService.save(role);
        }
        return "redirect:/role/list";
    }
}