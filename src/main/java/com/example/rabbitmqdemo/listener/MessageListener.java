package com.example.rabbitmqdemo.listener;

import com.example.rabbitmqdemo.config.RabbitConfig;
import com.example.rabbitmqdemo.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j // Lombok이 log라는 Logger 객체를 자동 생성
@Component // 이 클래스는 Spring이 관리하는 Bean으로 등록됨
public class MessageListener {
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME) //demo.queue로 부터 메시지가 오면 자동 호출된다.
    public void receiveMessage(MessageDto messageDto){
        log.info("수신자 : {}", messageDto.getName());
        log.info("수신한 메시지 : {}", messageDto.getMessage());
    }
}