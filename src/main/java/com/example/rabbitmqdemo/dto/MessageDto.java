package com.example.rabbitmqdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok어노테이션으로, Getter, Setter, toString, equals를 자동생성해줌.
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 자동 생성
public class MessageDto{
    private String name;
    private String message;
}