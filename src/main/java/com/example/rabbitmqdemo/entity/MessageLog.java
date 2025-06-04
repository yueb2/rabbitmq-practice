package com.example.rabbitmqdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity //JPA 엔티티 클래스임을 나타냄
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder //MessageLog.builder()로 객체 생성 가능
public class MessageLog {

    @Id // 기본 키 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동증가전략 설정
    private Long id;

    private String name;

    private String message;

    private LocalDateTime receivedAt; //수신 시간
}
