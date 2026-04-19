package com.example.adminsystem.controller;

import com.example.adminsystem.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/operation-log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page, 
                      @RequestParam(defaultValue = "10") int size, 
                      Model model) {
        Pageable pageable = PageRequest.of(page, size);
        var logs = operationLogService.findAll(pageable);
        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        return "operation-log/list";
    }
}