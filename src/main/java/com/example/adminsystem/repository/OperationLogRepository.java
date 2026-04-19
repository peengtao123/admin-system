package com.example.adminsystem.repository;

import com.example.adminsystem.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    Page<OperationLog> findAllByOrderByCreateTimeDesc(Pageable pageable);
}