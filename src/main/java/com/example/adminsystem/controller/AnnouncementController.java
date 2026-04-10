package com.example.adminsystem.controller;

import com.example.adminsystem.entity.Announcement;
import com.example.adminsystem.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/announcement")
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;
    
    @GetMapping("/list")
    public String list(Model model) {
        List<Announcement> announcements = announcementService.findAll();
        model.addAttribute("announcements", announcements);
        return "announcement/list";
    }
    
    @GetMapping("/form")
    public String form(Model model, @RequestParam(required = false) Long id) {
        if (id != null) {
            Announcement announcement = announcementService.findById(id).orElse(new Announcement());
            model.addAttribute("announcement", announcement);
        } else {
            model.addAttribute("announcement", new Announcement());
        }
        return "announcement/form";
    }
    
    @PostMapping("/save")
    public String save(@NonNull Announcement announcement) {
        announcementService.save(announcement);
        return "redirect:/announcement/list";
    }
    
    @GetMapping("/delete")
    public String delete(@NonNull @RequestParam Long id) {
        announcementService.deleteById(id);
        return "redirect:/announcement/list";
    }
}