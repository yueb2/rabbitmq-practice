package com.example.rabbitmqdemo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 이 클래스가 Spring 설정 클래스 임을 의미.
public class RabbitConfig {
    // 큐이름, 익스체인지 이름, 라우팅 키를 상수로 선언하여 한곳에서 관리.
    public static final String QUEUE_NAME = "demo.queue";
    public static final String EXCHANGE_NAME = "demo.exchange";
    public static final String ROUTING_KEY = "demo.routingkey";

    @Bean // 이 메서드가 리턴하는 객체를 srping 컨테이너가 관리.
    public Queue queue(){
        return new Queue(QUEUE_NAME, false); // 이름이 QUEUE_NAME(demo.queue)인 큐를 durable=false(서버를 껏다가 켜면 큐가 사라짐)로 생성
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE_NAME); // 라우팅 키를 기준으로 메시지를 정확하게 라우팅 하는 방식의 교환기(exchange)
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY); // 익스체인지와 큐를 ROUTING_KEY(demo.routingkey)로 바인딩.
    }
}