package com.example.adminsystem.repository;

import com.example.adminsystem.entity.ProcessInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessInstanceRepository extends JpaRepository<ProcessInstance, Long> {
    ProcessInstance findByProcessInstanceId(String processInstanceId);
    List<ProcessInstance> findByStatus(String status);
    List<ProcessInstance> findByStartedBy(String startedBy);
}