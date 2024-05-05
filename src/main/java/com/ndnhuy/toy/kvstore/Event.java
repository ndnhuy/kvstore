package com.ndnhuy.toy.kvstore;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
@Getter
public class Event {
    private EventType type;
    private String key;
    private String value;

    public boolean isPut() {
        return EventType.PUT == type;
    }

    public boolean isDelete() {
        return EventType.DELETE == type;
    }
}
