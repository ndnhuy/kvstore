package com.ndnhuy.toy.kvstore.pubsub;

import com.ndnhuy.toy.kvstore.Event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public interface PubSub {
    void publish(Event event);

    void subscribe(Consumer<Event> eventHandler);

    void shutdown();
}
