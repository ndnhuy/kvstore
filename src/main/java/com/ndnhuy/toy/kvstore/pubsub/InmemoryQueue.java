package com.ndnhuy.toy.kvstore.pubsub;

import com.ndnhuy.toy.kvstore.Event;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class InmemoryQueue implements PubSub {

    private ArrayBlockingQueue<Event> queue = new ArrayBlockingQueue<>(100);

    private boolean isRunning;

    @Override
    public void publish(Event event) {
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribe(Consumer<Event> eventHandler) {
        new TaskWorker(eventHandler).start();
    }

    @Override
    public void shutdown() {
        this.isRunning = false;
    }

    private class TaskWorker extends Thread {

        private final Consumer<Event> eventHandler;

        TaskWorker(Consumer<Event> eventHandler) {
            this.eventHandler = eventHandler;
        }

        @Override
        public void run() {
            isRunning = true;
            while (isRunning) {
                Optional<Event> item = take();
                item.ifPresent(eventHandler);
            }
        }

        private Optional<Event> take() {
            try {
                return Optional.ofNullable(queue.poll(2, TimeUnit.MILLISECONDS));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
