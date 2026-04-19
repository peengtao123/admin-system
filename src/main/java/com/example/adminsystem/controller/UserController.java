package com.example.adminsystem.controller;

import com.example.adminsystem.entity.Role;
import com.example.adminsystem.entity.User;
import com.example.adminsystem.service.UserService;
import com.example.adminsystem.service.DepartmentService;
import com.example.adminsystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private RoleService roleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("departments", departmentService.findAll());
        return "user/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("roles", roleService.findAll());
        return "user/form";
    }

    @PostMapping
    public String save(@ModelAttribute User user, @RequestParam(value = "roleIds", required = false) List<Long> roleIds) {
        if (roleIds != null) {
            List<Role> roles = roleService.findAllById(roleIds);
            user.setRoles(roles);
        }
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("roles", roleService.findAll());
        return "user/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}