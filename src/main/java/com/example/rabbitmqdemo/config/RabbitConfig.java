package com.example.rabbitmqdemo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration // 이 클래스가 Spring 설정 클래스 임을 의미.
public class RabbitConfig {
    // 큐이름, 익스체인지 이름, 라우팅 키를 상수로 선언하여 한곳에서 관리.
    public static final String QUEUE_NAME = "main.queue";
    public static final String EXCHANGE_NAME = "main.exchange";
    public static final String DLX_EXCHANGE_NAME = "dlx.exchange";
    public static final String DLQ_QUEUE = "dlq.queue";
    public static final String ROUTING_KEY = "main.routing.key";
    public static final String DLX_ROUTING_KEY = "dlx.routing.key";
    public static final String FANOUT_EXCHANGE_NAME = "fanout.exchange";
    public static final String FANOUT_QUEUE_A = "fanout.queue.A";
    public static final String FANOUT_QUEUE_B = "fanout.queue.B";
    public static final String TOPIC_EXCHANGE_NAME = "topic.exchange";
    public static final String USER_QUEUE = "topic.user.queue";
    public static final String ORDER_QUEUE = "topic.order.queue";

    // delay queue : TTL + DLX 설정 포함
    @Bean
    public Queue delayQueue(){
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

    @Bean
    public Queue dlqQueue() {
        return new Queue(DLQ_QUEUE); //예외처리 큐
    }

    // 기존 큐 (Consumer 구독중)
    @Bean
    public Queue realQueue(){
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue fanoutQueueA() {
        return new Queue(FANOUT_QUEUE_A);
    }

    @Bean
    public Queue fanoutQueueB() {
        return new Queue(FANOUT_QUEUE_B);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE);
    }

    // queue1 : color.red
    @Bean
    public Queue redQueue() {
        return new Queue("topic.queue.red");
    }

    // queue2 : color.* (* : 단어하나 일치)
    @Bean
    public Queue colorAllQueue(){
        return new Queue("topic.queue.color.all");
    }

    //queue3 : color.# (# : 0개이상 모든 단어 일치)
    @Bean
    public Queue colorDeepQueue(){
        return new Queue("topic.queue.color.deep");
    }

    // delay queue 바인딩
    @Bean
    public Binding dealyBinding(){
        return  BindingBuilder.bind(delayQueue()).to(dlxExchange()).with("delay.routing.key");
    }

    // 실 queue 바인딩
    @Bean
    public Binding realBinding(){
        return  BindingBuilder.bind(realQueue()).to(mainExchange()).with(ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding(){
        return BindingBuilder.bind(dlqQueue()).to(dlxExchange()).with(DLX_ROUTING_KEY);
    }

    @Bean
    public Binding fanoutBindingA() {
        return BindingBuilder.bind(fanoutQueueA()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBindingB() {
        return BindingBuilder.bind(fanoutQueueB()).to(fanoutExchange());
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue()).to(topicExchange()).with("user.#");
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(topicExchange()).with("order.#");
    }

    @Bean
    public Binding redBinding() {
        return BindingBuilder.bind(redQueue()).to(topicExchange()).with("color.red");
    }

    @Bean
    public Binding colorAllBinding() {
        return BindingBuilder.bind(colorAllQueue()).to(topicExchange()).with("color.*");
    }

    @Bean
    public Binding colorDeepBinding() {
        return BindingBuilder.bind(colorDeepQueue()).to(topicExchange()).with("color.#");
    }

    @Bean
    public DirectExchange mainExchange(){
        return new DirectExchange(EXCHANGE_NAME); // 라우팅 키를 기준으로 메시지를 정확하게 라우팅 하는 방식의 교환기(exchange)
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE_NAME);
    }

    //연결된 모두에게 메세지를 브로드캐스트
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
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