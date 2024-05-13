package com.ndnhuy.toy.kvstore.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndnhuy.toy.kvstore.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BroadcastEvent extends ApplicationEvent {

    private final Event event;

    public BroadcastEvent(Object source, String message) {
        super(source);
        try {
            this.event = new ObjectMapper().readValue(message, Event.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("fail to parse message: " + message, e);
        }
    }
}
