package com.example.rabbitmqdemo.controller;

import com.example.rabbitmqdemo.config.RabbitConfig;
import com.example.rabbitmqdemo.dto.MessageDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스는 RESTful 웹 서비스의 컨트롤러 이다.
@RequestMapping("/message") // 이 클래스의 모든 요청 url은 '/message'로 시작한다.
public class MessageController {
    private  final RabbitTemplate rabbitTemplate; // message 전송에 사용되는 Spring 제공 클래스, 큐로 메시지를 보낼때 사용한다.

    public MessageController(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate; // 생성자 주입 방식으로 의존성 주입
    }

    @PostMapping // HTTP POST 요청 처리
    public String sendMessage(@RequestBody MessageDto messageDto){
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, messageDto);
        return "메시지 전송 완료 : " + messageDto.getMessage();
    }

    @PostMapping("/delay")
    public String sendDelayMessage(@RequestBody MessageDto messageDto){
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "delay.routing.key", messageDto);
        return "지연 메시지 전송 완료 (10초 후 처리 예정)";
    }

    @PostMapping("/fail")
    public String sendFailMessage(@RequestBody MessageDto messageDto){
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "main.routing.key", messageDto);
        return "실패 메시지 전송 시도";
    }

    @PostMapping("/fanout")
    public String sendFanout(@RequestBody MessageDto messageDto){
        //fanout은 routingkey를 무시하므로 ""(빈문자열사용)
        rabbitTemplate.convertAndSend(RabbitConfig.FANOUT_EXCHANGE_NAME, "", messageDto);
        return "팬아웃 메세지 전송 완료!";
    }

    @PostMapping("/topic")
    public String sendTopic(@RequestParam String routingKey, @RequestBody MessageDto messageDto){
        rabbitTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE_NAME, routingKey, messageDto);
        return "토픽 메세지 전송 완료 ("+routingKey+")";
    }
}