package com.ndnhuy.toy.kvstore.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Getter
@Component
@Slf4j
@AllArgsConstructor
public class Receiver implements MessageReceiver {

    private ApplicationEventPublisher publisher;

    public void receiveMessage(String message) {
        log.info("receive message: " + message);
        publisher.publishEvent(new BroadcastEvent("kvstore", message));
    }
}
