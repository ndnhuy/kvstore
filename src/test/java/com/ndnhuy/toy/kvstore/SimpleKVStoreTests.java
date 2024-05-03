package com.ndnhuy.toy.kvstore;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleKVStoreTests {

    private final InmemoryKVLogger kvLogger = new InmemoryKVLogger();
    private final SimpleKVStore kvStore = new SimpleKVStore(kvLogger);

    @Test
    void testStore() {
        kvStore.put("k1", "v1");
        assertThat(kvStore.get("k1")).isEqualTo("v1");
        assertEventsEquals(List.of("PUT,k1,v1"));
        kvStore.put("k1", "v2");
        assertThat(kvStore.get("k1")).isEqualTo("v2");
        assertEventsEquals(List.of("PUT,k1,v1", "PUT,k1,v2"));
        kvStore.delete("k1");
        assertThat(kvStore.get("k1")).isNull();
        assertEventsEquals(List.of("PUT,k1,v1", "PUT,k1,v2", "DELETE,k1"));
    }

    private void assertEventsEquals(List<String> eventsStr) {
        var wantEvents = eventsStr.stream().map(str -> str.split(",")).map(vals -> Event.builder()
                        .type(EventType.valueOf(vals[0]))
                        .key(vals[1])
                        .value(vals.length > 2 ? vals[2] : null)
                        .build())
                .collect(Collectors.toList());
        assertThat(kvLogger.getEvents()).isEqualTo(wantEvents);
    }

}
