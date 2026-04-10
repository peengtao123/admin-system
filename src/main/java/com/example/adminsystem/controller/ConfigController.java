package com.example.adminsystem.controller;

import com.example.adminsystem.entity.Config;
import com.example.adminsystem.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/config")
public class ConfigController {
    @Autowired
    private ConfigService configService;
    
    @GetMapping("/list")
    public String list(Model model) {
        List<Config> configs = configService.findAll();
        model.addAttribute("configs", configs);
        return "config/list";
    }
    
    @GetMapping("/form")
    public String form(Model model, @RequestParam(required = false) Long id) {
        if (id != null) {
            Config config = configService.findById(id).orElse(new Config());
            model.addAttribute("config", config);
        } else {
            model.addAttribute("config", new Config());
        }
        return "config/form";
    }
    
    @PostMapping("/save")
    public String save(@NonNull Config config) {
        configService.save(config);
        return "redirect:/config/list";
    }
    
    @GetMapping("/delete")
    public String delete(@NonNull @RequestParam Long id) {
        configService.deleteById(id);
        return "redirect:/config/list";
    }
}