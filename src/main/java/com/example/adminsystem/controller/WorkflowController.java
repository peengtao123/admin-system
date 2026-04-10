package com.example.adminsystem.controller;

import com.example.adminsystem.entity.ProcessInstance;
import com.example.adminsystem.service.WorkflowService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {
    @Autowired
    private WorkflowService workflowService;
    
    @GetMapping("/deploy")
    public String deployForm() {
        return "workflow/deploy";
    }
    
    @PostMapping("/deploy")
    public String deploy(MultipartFile file, String name, Model model) throws IOException {
        Deployment deployment = workflowService.deployProcess(file, name);
        model.addAttribute("message", "流程部署成功：" + deployment.getName());
        return "workflow/deploy";
    }
    
    @GetMapping("/process-definitions")
    public String processDefinitions(Model model) {
        List<ProcessDefinition> processDefinitions = workflowService.getProcessDefinitions();
        model.addAttribute("processDefinitions", processDefinitions);
        return "workflow/process-definitions";
    }
    
    @GetMapping("/start")
    public String startForm(Model model) {
        List<ProcessDefinition> processDefinitions = workflowService.getProcessDefinitions();
        model.addAttribute("processDefinitions", processDefinitions);
        return "workflow/start";
    }
    
    @PostMapping("/start")
    public String start(@RequestParam String processDefinitionKey, 
                        @RequestParam String businessKey, 
                        @RequestParam Map<String, String> variables, 
                        Model model) {
        // 转换variables为Map<String, Object>
        Map<String, Object> vars = new java.util.HashMap<>();
        variables.forEach((k, v) -> {
            if (!k.equals("processDefinitionKey") && !k.equals("businessKey")) {
                vars.put(k, v);
            }
        });
        
        ProcessInstance processInstance = workflowService.startProcess(processDefinitionKey, businessKey, vars, "admin");
        model.addAttribute("message", "流程启动成功：" + processInstance.getName());
        return "workflow/start";
    }
    
    @GetMapping("/tasks")
    public String tasks(Model model) {
        List<Task> tasks = workflowService.getTasks("admin");
        model.addAttribute("tasks", tasks);
        return "workflow/tasks";
    }
    
    @GetMapping("/complete-task")
    public String completeTaskForm(@RequestParam String taskId, Model model) {
        model.addAttribute("taskId", taskId);
        return "workflow/complete-task";
    }
    
    @PostMapping("/complete-task")
    public String completeTask(@RequestParam String taskId, 
                               @RequestParam Map<String, String> variables, 
                               Model model) {
        // 转换variables为Map<String, Object>
        Map<String, Object> vars = new java.util.HashMap<>();
        variables.forEach((k, v) -> {
            if (!k.equals("taskId")) {
                vars.put(k, v);
            }
        });
        
        workflowService.completeTask(taskId, vars);
        model.addAttribute("message", "任务完成成功");
        return "workflow/complete-task";
    }
    
    @GetMapping("/process-instances")
    public String processInstances(Model model) {
        List<ProcessInstance> processInstances = workflowService.getProcessInstances();
        model.addAttribute("processInstances", processInstances);
        return "workflow/process-instances";
    }
}