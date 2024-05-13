package com.ndnhuy.toy.kvstore;

import com.ndnhuy.toy.kvstore.cluster.ClusterMember;
import com.ndnhuy.toy.kvstore.cluster.KVCluster;
import com.ndnhuy.toy.kvstore.cluster.GossipKVCluster;
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
        // 2 services kvstore1 and kvstore2
        var kvStore1 = new SimpleKVStore("kvstore1", kvLogger);
        var kvStore2 = new SimpleKVStore("kvstore2", kvLogger);
        var remoteKVStore1 = new SimpleRemoteKVStore("remote-kvstore1", kvStore1);
        var remoteKVStore2 = new SimpleRemoteKVStore("remote-kvstore2", kvStore2);
        var pubSub = new InmemoryQueue();
        pubSub.start();
        // create first cluster that holds reference to kvstore1 and kvstore2
        var cluster1 = createCluster(kvStore1, remoteKVStore2, pubSub);
        // create second cluster that holds reference to kvstore1 and kvstore2
        var cluster2 = createCluster(kvStore2, remoteKVStore1, pubSub);

        clusterShouldContains(cluster1, kvStore1, List.of(remoteKVStore2));
        clusterShouldContains(cluster2, kvStore2, List.of(remoteKVStore1));

        try {
            // put data to kvstore1 in cluster1
            kvStore1.put("k1", "v1");
            Thread.sleep(3000);
            assertThat(kvStore2.get("k1")).isEqualTo("v1"); // kvStore2 should call get key to kvStore kvStore2B
            assertThat(remoteKVStore1.get("k1")).isEqualTo("v1");
            assertThat(remoteKVStore2.get("k1")).isEqualTo("v1");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            pubSub.shutdown();
        }
    }

    private void clusterShouldContains(KVCluster cluster, ClusterMember wantLocal, List<ClusterMember> wantExternals) {
        assertThat(cluster.getClusterManager().getLocalMember().getId()).isEqualTo(wantLocal.getId());
        var externals = cluster.getClusterManager().getExternalMembers();
        var wantIds = wantExternals.stream().map(ClusterMember::getId).collect(Collectors.toList());
        assertThat(externals.stream().map(ClusterMember::getId)).containsAll(wantIds);

    }

    private GossipKVCluster createCluster(ClusterMember local, ClusterMember external, PubSub pubSub) {
        var c = new GossipKVCluster(pubSub);
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
