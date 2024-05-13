package com.ndnhuy.toy.kvstore.rabbitmq;

public interface MessageReceiver {
    void receiveMessage(String message);
}
