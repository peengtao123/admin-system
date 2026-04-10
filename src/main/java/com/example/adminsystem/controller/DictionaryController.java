package com.example.adminsystem.controller;

import com.example.adminsystem.entity.Dictionary;
import com.example.adminsystem.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dictionary")
public class DictionaryController {
    @Autowired
    private DictionaryService dictionaryService;
    
    @GetMapping("/list")
    public String list(Model model) {
        List<Dictionary> dictionaries = dictionaryService.findAll();
        model.addAttribute("dictionaries", dictionaries);
        return "dictionary/list";
    }
    
    @GetMapping("/form")
    public String form(Model model, @RequestParam(required = false) Long id) {
        if (id != null) {
            Dictionary dictionary = dictionaryService.findById(id).orElse(new Dictionary());
            model.addAttribute("dictionary", dictionary);
        } else {
            model.addAttribute("dictionary", new Dictionary());
        }
        return "dictionary/form";
    }
    
    @PostMapping("/save")
    public String save(@NonNull Dictionary dictionary) {
        dictionaryService.save(dictionary);
        return "redirect:/dictionary/list";
    }
    
    @GetMapping("/delete")
    public String delete(@NonNull @RequestParam Long id) {
        dictionaryService.deleteById(id);
        return "redirect:/dictionary/list";
    }
}