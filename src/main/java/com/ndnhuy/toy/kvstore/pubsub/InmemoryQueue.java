package com.ndnhuy.toy.kvstore.pubsub;

import com.ndnhuy.toy.kvstore.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * inmemory queue, for testing purpose only
 */
public class InmemoryQueue implements PubSub {

    private final ArrayBlockingQueue<Event> routingQueue = new ArrayBlockingQueue<>(100);

    private final TaskWorker taskWorker = new TaskWorker();

    private boolean isRunning;

    @Override
    public void publish(Event event) {
        try {
            routingQueue.put(event);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribe(Consumer<Event> eventHandler) {
        taskWorker.registerHandler(eventHandler);
    }

    @Override
    public void start() {
        if (!isRunning) {
            this.taskWorker.start();
        }
    }

    @Override
    public void shutdown() {
        this.isRunning = false;
    }

    private class TaskWorker extends Thread {

        private final List<Consumer<Event>> eventHandlers = new ArrayList<>();

        void registerHandler(Consumer<Event> eventHandler) {
            this.eventHandlers.add(eventHandler);
        }

        @Override
        public void run() {
            isRunning = true;
            while (isRunning) {
                Optional<Event> item = take();
                item.ifPresent(event -> eventHandlers.forEach(handler -> handler.accept(event)));
            }
        }

        private Optional<Event> take() {
            try {
                return Optional.ofNullable(routingQueue.poll(2, TimeUnit.MILLISECONDS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
