package com.example.rabbitmqdemo.listener;

import lombok.extern.slf4j.Slf4j; // lombok에서 제공하는 로그 객체 자동 생성.
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 이 클래스는 Spring이 관리하는 Bean으로 등록됨
public class MessageListener {
    @RabbitListener(queues = "demo.queue") //demo.queue로 부터 메시지가 오면 자동 호출된다.
    public void receiveMessage(String message){
        log.info("수신한 메시지 : {}",message);
    }
}