package com.example.rabbitmqdemo.listener;

import com.example.rabbitmqdemo.config.*;
import com.example.rabbitmqdemo.dto.*;
import com.example.rabbitmqdemo.entity.*;
import com.example.rabbitmqdemo.repository.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;

@Slf4j // Lombok이 log라는 Logger 객체를 자동 생성
@Component // 이 클래스는 Spring이 관리하는 Bean으로 등록됨
public class MessageListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageLogRepository messageLogRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME) //main.queue로 부터 메시지가 오면 자동 호출된다.
    public void receiveMessage(MessageDto messageDto){
        if(messageDto.getMessage().contains("fail")){ // fail이라는 단어가 포함되면 실패 처리.
            log.info("예외 발생! DLQ로 이동");
            throw new RuntimeException("강제 실패");
        }
        log.info("수신자 : {}", messageDto.getName());
        log.info("수신한 메시지 : {}", messageDto.getMessage());

        //db에 log 저장
        MessageLog logEntity = MessageLog.builder()
                .name(messageDto.getName())
                .message(messageDto.getMessage())
                .receivedAt(LocalDateTime.now())
                .build();

        messageLogRepository.save(logEntity);

        //log파일에 log저장
        /*
        try {
            String logLine = String.format("수신자 : %s, 메시지 : %s\n", messageDto.getName(), messageDto.getMessage());
            Files.write(
                    Paths.get("messages.log"), //현재프로젝트 루트에 log파일 경로 지정.
                    logLine.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        }catch (Exception e){
            log.error("파일 저장 실패", e);
        }
        */
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

    @RabbitListener(queues = RabbitConfig.FANOUT_QUEUE_A)
    public void receiveFanoutA(MessageDto messageDto){
        log.info("[A 큐] 수신 : {} ",  messageDto.getMessage());
    }

    @RabbitListener(queues = RabbitConfig.FANOUT_QUEUE_B)
    public void receiveFanoutB(MessageDto messageDto){
        log.info("[B 큐] 수신 : {} ",  messageDto.getMessage());
    }

    @RabbitListener(queues = RabbitConfig.USER_QUEUE)
    public void receiveUserTopic(MessageDto messageDto){
        log.info("[user.queue] 수신: {}", messageDto.getMessage());
    }

    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void receiveOrderTopic(MessageDto messageDto){
        log.info("[order.queue] 수신: {}", messageDto.getMessage());
    }

    @RabbitListener(queues = "topic.queue.red")
    public void receiveRedTopic(MessageDto messageDto){
        log.info("[topic.queue.red] 수신자: {}, 메시지: {}", messageDto.getName(), messageDto.getMessage());
    }

    @RabbitListener(queues = "topic.queue.color.all")
    public void receiveColorAllTopic(MessageDto messageDto){
        log.info("[topic.queue.color.all] 수신자: {}, 메시지: {}", messageDto.getName(), messageDto.getMessage());
    }

    @RabbitListener(queues = "topic.queue.color.deep")
    public void receiveColorDeepTopic(MessageDto messageDto){
        log.info("[topic.queue.color.deep] 수신자: {}, 메시지: {}", messageDto.getName(), messageDto.getMessage());
    }
}