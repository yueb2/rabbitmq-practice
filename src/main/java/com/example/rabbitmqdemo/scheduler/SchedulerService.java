package com.example.rabbitmqdemo.scheduler;

import com.example.rabbitmqdemo.config.*;
import com.example.rabbitmqdemo.dto.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerService {
    private final RabbitTemplate rabbitTemplate;

    private int count = 1;

    //10초마다 실행
    @Scheduled(fixedRate = 10000)
    public void sendScheduledMessage(){
        MessageDto messageDto = new MessageDto("Scheduler", "자동 전송 메시지 #" + count++,0);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, messageDto);
        log.info("[배치 전송] 메시지 전송 완료: {}", messageDto.getMessage());
    }
}
