package com.ndnhuy.toy.kvstore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true, value = {"put", "delete"})
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
