package com.example.adminsystem.service;

import com.example.adminsystem.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface OperationLogService {
    Page<OperationLog> findAll(Pageable pageable);
    @Nullable
    OperationLog save(OperationLog operationLog);
}