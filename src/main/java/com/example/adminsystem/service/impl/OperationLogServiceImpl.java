package com.example.adminsystem.service.impl;

import com.example.adminsystem.entity.OperationLog;
import com.example.adminsystem.repository.OperationLogRepository;
import com.example.adminsystem.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Override
    public Page<OperationLog> findAll(Pageable pageable) {
        return operationLogRepository.findAllByOrderByCreateTimeDesc(pageable);
    }

    @Override
    @SuppressWarnings("null")
    public OperationLog save(OperationLog operationLog) {
        return operationLogRepository.save(operationLog);
    }
}