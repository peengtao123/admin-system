package com.example.adminsystem.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "process_instance")
public class ProcessInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "process_instance_id", unique = true)
    private String processInstanceId;
    
    @Column(name = "process_definition_id")
    private String processDefinitionId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "business_key")
    private String businessKey;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "started_by")
    private String startedBy;
    
    @Column(name = "started_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startedTime;
    
    @Column(name = "completed_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedTime;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }
    
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBusinessKey() {
        return businessKey;
    }
    
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStartedBy() {
        return startedBy;
    }
    
    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }
    
    public Date getStartedTime() {
        return startedTime;
    }
    
    public void setStartedTime(Date startedTime) {
        this.startedTime = startedTime;
    }
    
    public Date getCompletedTime() {
        return completedTime;
    }
    
    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }
}