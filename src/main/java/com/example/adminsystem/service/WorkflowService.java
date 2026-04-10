package com.example.adminsystem.service;

import com.example.adminsystem.repository.ProcessInstanceRepository;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;

import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ProcessInstanceRepository processInstanceRepository;
    
    // 部署流程
    public Deployment deployProcess(MultipartFile file, String name) throws IOException {
        return repositoryService.createDeployment()
                .name(name)
                .addInputStream(file.getOriginalFilename(), file.getInputStream())
                .deploy();
    }
    
    // 获取所有流程定义
    public List<ProcessDefinition> getProcessDefinitions() {
        return repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list();
    }
    
    // 启动流程实例
    public com.example.adminsystem.entity.ProcessInstance startProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables, String startedBy) {
        org.flowable.engine.runtime.ProcessInstance flowableProcessInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        
        com.example.adminsystem.entity.ProcessInstance processInstance = new com.example.adminsystem.entity.ProcessInstance();
        processInstance.setProcessInstanceId(flowableProcessInstance.getId());
        processInstance.setProcessDefinitionId(flowableProcessInstance.getProcessDefinitionId());
        
        // 设置流程实例名称，优先使用变量中的员工姓名和请假类型
        String instanceName = flowableProcessInstance.getName();
        if (instanceName == null) {
            if (variables.containsKey("employeeName") && variables.containsKey("leaveType")) {
                instanceName = variables.get("employeeName") + "的" + getLeaveTypeName((String) variables.get("leaveType")) + "申请";
            } else {
                instanceName = "流程实例" + flowableProcessInstance.getId();
            }
        }
        processInstance.setName(instanceName);
        
        processInstance.setBusinessKey(flowableProcessInstance.getBusinessKey());
        processInstance.setStatus("RUNNING");
        processInstance.setStartedBy(startedBy);
        processInstance.setStartedTime(new Date());
        
        return processInstanceRepository.save(processInstance);
    }
    
    // 获取请假类型的中文名称
    private String getLeaveTypeName(String leaveType) {
        switch (leaveType) {
            case "annual":
                return "年假";
            case "sick":
                return "病假";
            case "personal":
                return "事假";
            case "other":
                return "其他";
            default:
                return leaveType;
        }
    }
    
    // 获取用户任务
    public List<Task> getTasks(String assignee) {
        return taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
    }
    
    // 完成任务
    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
    
    // 获取流程实例
    public List<com.example.adminsystem.entity.ProcessInstance> getProcessInstances() {
        return processInstanceRepository.findAll();
    }
    
    // 获取运行中的流程实例
    public List<com.example.adminsystem.entity.ProcessInstance> getRunningProcessInstances() {
        return processInstanceRepository.findByStatus("RUNNING");
    }
    
    // 获取已完成的流程实例
    public List<com.example.adminsystem.entity.ProcessInstance> getCompletedProcessInstances() {
        return processInstanceRepository.findByStatus("COMPLETED");
    }
    
    // 根据用户获取流程实例
    public List<com.example.adminsystem.entity.ProcessInstance> getProcessInstancesByUser(String startedBy) {
        return processInstanceRepository.findByStartedBy(startedBy);
    }
}