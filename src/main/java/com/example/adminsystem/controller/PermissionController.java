package com.example.adminsystem.controller;

import com.example.adminsystem.entity.Permission;
import com.example.adminsystem.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page, 
                      @RequestParam(defaultValue = "10") int size, 
                      Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Permission> permissions = permissionService.findAll(pageable);
        model.addAttribute("permissions", permissions);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "permission/list";
    }

    @GetMapping("/form")
    public String form(@RequestParam(value = "id", required = false) Long id, Model model) {
        if (id != null) {
            model.addAttribute("permission", permissionService.findById(id));
        } else {
            model.addAttribute("permission", new Permission());
        }
        List<Permission> menuPermissions = permissionService.findByType("menu");
        model.addAttribute("menuPermissions", menuPermissions);
        return "permission/form";
    }

    @PostMapping("/save")
    public String save(Permission permission) {
        permissionService.save(permission);
        return "redirect:/permission/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        permissionService.deleteById(id);
        return "redirect:/permission/list";
    }
}