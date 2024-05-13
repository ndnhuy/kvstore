package com.ndnhuy.toy.kvstore.pubsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndnhuy.toy.kvstore.Event;
import com.ndnhuy.toy.kvstore.rabbitmq.BroadcastEvent;
import lombok.NonNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.ndnhuy.toy.kvstore.rabbitmq.RabbitMQConfiguration.topicExchangeName;

public class BroadcastPubSub implements PubSub, ApplicationListener<BroadcastEvent> {

    private final List<Consumer<Event>> eventHandlers = new ArrayList<>();

    private final RabbitTemplate rabbitTemplate;

    public BroadcastPubSub(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Event event) {
        String eventAsJson = null;
        try {
            eventAsJson = new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        rabbitTemplate.convertAndSend(topicExchangeName, "kvstore.message", eventAsJson);
    }

    @Override
    public void subscribe(Consumer<Event> eventHandler) {
        this.eventHandlers.add(eventHandler);
    }

    @Override
    public void start() {
        // do nothing
    }

    @Override
    public void shutdown() {
        // do nothing
    }

    @Override
    public void onApplicationEvent(@NonNull BroadcastEvent broadcastEvent) {
        this.eventHandlers.forEach(handler -> handler.accept(broadcastEvent.getEvent()));
    }
}
