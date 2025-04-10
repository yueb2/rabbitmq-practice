package com.example.rabbitmqdemo.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 이 클래스는 RESTful 웹 서비스의 컨트롤러 이다.
@RequestMapping("/message") // 이 클래스의 모든 요청 url은 '/message'로 시작한다.
public class MessageController {
    private final RabbitTemplate rabbitTemplate; // message 전송에 사용되는 Spring 제공 클래스, 큐로 메시지를 보낼때 사용한다.

    public MessageController(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate; // 생성자 주입 방식으로 의존성 주입
    }

    @PostMapping // HTTP POST 요청 처리
    public ResponseEntity<String> sendMessage(String message){
        String exchange = "demo.exchange";
        String routingKey = "demo.routingkey";

        /*
         message를 demo.exchange로 전송한다.
         demo.routingkey를 기준으로 해당 큐에 message가 전달된다.
         */
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return ResponseEntity.ok(message); // 클라이언트에게 '전송 성공' 응답반환.
    }
}