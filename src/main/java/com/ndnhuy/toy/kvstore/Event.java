package com.ndnhuy.toy.kvstore;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
public class Event {
    private EventType type;
    private String key;
    private String value;
}
