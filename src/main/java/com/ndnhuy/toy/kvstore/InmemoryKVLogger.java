package com.ndnhuy.toy.kvstore;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InmemoryKVLogger implements KVLogger<String, String> {

    private final List<Event> events = new ArrayList<>();

    @Override
    public void writePut(String key, String value) {
        events.add(Event.builder()
                .key(key)
                .value(value)
                .type(EventType.PUT)
                .build());
    }

    @Override
    public void writeDelete(String key) {
        events.add(Event.builder()
                .key(key)
                .type(EventType.DELETE)
                .build());
    }

}
