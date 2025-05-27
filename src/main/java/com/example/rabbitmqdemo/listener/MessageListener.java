package com.example.rabbitmqdemo.listener;

import com.example.rabbitmqdemo.config.RabbitConfig;
import com.example.rabbitmqdemo.dto.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j // Lombok이 log라는 Logger 객체를 자동 생성
@Component // 이 클래스는 Spring이 관리하는 Bean으로 등록됨
public class MessageListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME) //main.queue로 부터 메시지가 오면 자동 호출된다.
    public void receiveMessage(MessageDto messageDto){
        if(messageDto.getMessage().contains("fail")){ // fail이라는 단어가 포함되면 실패 처리.
            log.info("예외 발생! DLQ로 이동");
            throw new RuntimeException("강제 실패");
        }
        log.info("수신자 : {}", messageDto.getName());
        log.info("수신한 메시지 : {}", messageDto.getMessage());
    }

    @RabbitListener(queues = RabbitConfig.DLQ_QUEUE)
    public void receiveDlq(MessageDto messageDto){
        log.info("[DLQ 수신] 실패 메시지: {} ",  messageDto.getMessage());

        if (messageDto.getRetryCount() > 3) {
            log.warn("재시도 한도 초과. 폐기 처리");
            return;
        }

        messageDto.setRetryCount(messageDto.getRetryCount() + 1);

        log.info("10초 후 재시도 큐로 재전송. 현재 재시도 횟수 : {}", messageDto.getRetryCount());

        //retry.routing.key로 라우팅되게 DLX 설정된 곳으로 전송
        rabbitTemplate.convertAndSend(RabbitConfig.DLX_EXCHANGE_NAME, "retry.routing.key", messageDto);
    }
}