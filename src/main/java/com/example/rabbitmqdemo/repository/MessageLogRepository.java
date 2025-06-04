package com.example.rabbitmqdemo.repository;

import com.example.rabbitmqdemo.entity.*;
import org.springframework.data.jpa.repository.*;

//JPA가 자동으로 CRUD메서드 제공해줌.
public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
}
