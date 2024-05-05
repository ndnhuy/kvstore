package com.ndnhuy.toy.kvstore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ndnhuy.toy.kvstore.cluster.ClusterMember;
import com.ndnhuy.toy.kvstore.cluster.NoopReplicator;
import com.ndnhuy.toy.kvstore.cluster.Replicator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimpleKVStore implements KVStore<String, String>, ClusterMember {

    private final String serviceId;

    private final Map<String, String> store = new ConcurrentHashMap<>();

    private final KVLogger<String, String> kvLogger;

    private Replicator replicator = new NoopReplicator();

    public SimpleKVStore(String serviceId, KVLogger<String, String> kvLogger) {
        this.kvLogger = kvLogger;
        this.serviceId = serviceId;
    }

    @Override
    public String get(String key) {
        var v = store.get(key);
        log.info("[{}] Get [{},{}]", serviceId, key, v);
        return v;
    }

    @Override
    public void put(String key, String value) {
        log.info("[{}] Put [{},{}]", serviceId, key, value);
        kvLogger.writePut(key, value);
        store.put(key, value);
        replicator.replicate(Event.builder().key(key).value(value).type(EventType.PUT).build());
    }

    public void putIfNotPresent(String key, String value) {
        if (!store.containsKey(key)) {
            put(key, value);
        }
    }

    @Override
    public void delete(String key) {
        log.info("Delete key {}", key);
        kvLogger.writeDelete(key);
        store.remove(key);
        replicator.replicate(Event.builder().key(key).type(EventType.DELETE).build());
    }

    public void deleteIfPresent(String key) {
        if (store.containsKey(key)) {
            delete(key);
        }
    }

    @Override
    public String getId() {
        return serviceId;
    }

    @Override
    public void accept(Replicator replicator) {
        this.replicator = replicator;
    }

    @Override
    public void leave() {
        this.replicator = new NoopReplicator();
    }

    @Override
    public void notify(Event event) {
        if (event.isPut()) {
            this.putIfNotPresent(event.getKey(), event.getValue());
        } else if (event.isDelete()) {
            this.deleteIfPresent(event.getKey());
        } else {
            throw new UnsupportedOperationException("unknown event type: " + event);
        }
    }

}
