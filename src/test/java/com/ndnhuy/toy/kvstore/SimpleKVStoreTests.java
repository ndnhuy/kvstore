package com.ndnhuy.toy.kvstore;

import com.ndnhuy.toy.kvstore.cluster.ClusterMember;
import com.ndnhuy.toy.kvstore.cluster.SimpleKVCluster;
import com.ndnhuy.toy.kvstore.pubsub.InmemoryQueue;
import com.ndnhuy.toy.kvstore.pubsub.PubSub;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class SimpleKVStoreTests {

    private final InmemoryCommitLog kvLogger = new InmemoryCommitLog();
    private final SimpleKVStore kvStore = new SimpleKVStore("1", kvLogger);

    @Test
    void testKVStore() {
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

    @Test
    void testKVStore_shouldWriteToCommitLogBeforePutToLocalStorage() {
        var mockKVLogger = mock(KVLogger.class);
        var kvStore = new SimpleKVStore("1", mockKVLogger);
        doThrow(KVLoggingException.class).when(mockKVLogger).writePut(any(), any());

        try {
            kvStore.put("k1", "v1");
        } catch (KVLoggingException e) {
            assertThat(kvStore.get("k1")).isNull();
            return;
        }
        fail("should not reach here");
    }

    @Test
    void testKVStoreClusterOf2_whenWritePutToOneService_allServicesShouldHaveData() {
        // service 1
        var kvStore1A = new SimpleKVStore("kvstore1", kvLogger);
        var kvStore2A = new SimpleKVStore("kvstore2", kvLogger);
        var kvStore1B = new SimpleKVStore("kvstore1", kvLogger);
        var kvStore2B = new SimpleKVStore("kvstore2", kvLogger);
        var pubSub = new InmemoryQueue();
        var cluster1 = createCluster(kvStore1A, kvStore2A, pubSub);
        var cluster2 = createCluster(kvStore2B, kvStore1B, pubSub);

        try {
            kvStore1A.put("k1", "v1");
            Thread.sleep(3000);
//            assertThat(kvStore2A.get("k1")).isEqualTo("v1"); // kvStore2A should call get key to kvStore kvStore2B
            assertThat(kvStore2B.get("k1")).isEqualTo("v1");
//            assertThat(kvStore1B.get("k1")).isEqualTo("v1"); // kvStore1B should call get key to kvStore 1A
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            pubSub.shutdown();
        }
    }

    private KVCluster createCluster(ClusterMember local, ClusterMember external, PubSub pubSub) {
        var c = new SimpleKVCluster(pubSub);
        c.registerLocalMember(local);
        c.registerExternalMember(external);
        return c;
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
