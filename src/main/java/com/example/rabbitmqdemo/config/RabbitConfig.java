package com.example.rabbitmqdemo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration // 이 클래스가 Spring 설정 클래스 임을 의미.
public class RabbitConfig {
    // 큐이름, 익스체인지 이름, 라우팅 키를 상수로 선언하여 한곳에서 관리.
    public static final String QUEUE_NAME = "demo.queue";
    public static final String EXCHANGE_NAME = "demo.exchange";
    public static final String ROUTING_KEY = "demo.routingkey";

    // delay queue : TTL + DLX 설정 포함
    @Bean
    public Queue dealyQueue(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", EXCHANGE_NAME); //TTL 만료 후 메세지를 보낼 대상
        args.put("x-dead-letter-routing-key", ROUTING_KEY); // 실제 큐로 보내기 위한 키
        args.put("x-message-ttl", 10000); // 10초(ms 단위)
        /*
         duable:false - 서버 재시작시 큐 유지 안됨.
         exclusive:false - 이 큐를 만든 연결에서만 사용가능하게 할지 여부. (보통 false)
         autoDelete:false - 큐가 더이상 사용되지 않을 때 자동 삭제할지 여부. (보통 false)
         argument:args - 지연 처리 설정의 핵심, TTL, DLX등 커스텀 설정을 담는 MAP
         */
        return new Queue("delay.queue", false, false, false, args);
    }

    // 기존 큐 (Consumer 구독중)
    @Bean
    public Queue realQueue(){
        return new Queue(QUEUE_NAME, false);
    }

    // delay queue 바인딩
    @Bean
    public Binding dealyBinding(){
        return  BindingBuilder.bind(dealyQueue()).to(exchange()).with("delay.routingkey");
    }

    // 실 queue 바인딩
    @Bean
    public Binding realBinding(){
        return  BindingBuilder.bind(realQueue()).to(exchange()).with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME); // 라우팅 키를 기준으로 메시지를 정확하게 라우팅 하는 방식의 교환기(exchange)
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}