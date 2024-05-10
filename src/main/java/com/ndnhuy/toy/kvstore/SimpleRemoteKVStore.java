package com.ndnhuy.toy.kvstore;

import com.ndnhuy.toy.kvstore.cluster.ClusterMember;
import com.ndnhuy.toy.kvstore.cluster.Replicator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleRemoteKVStore implements KVStore<String, String>, ClusterMember {

    private String serviceId;

    private KVStore<String, String> delegate;

    @Override
    public String get(String key) {
        return delegate.get(key);
    }

    @Override
    public void put(String key, String value) {
        delegate.put(key, value);
    }

    @Override
    public void delete(String key) {
        delegate.delete(key);
    }

    @Override
    public String getId() {
        return serviceId;
    }

    @Override
    public void accept(Replicator replicator) {
        // do nothing since the remote won't replicate anything
    }

    @Override
    public void leave() {
        // do nothing
    }

    @Override
    public void receive(Event event) {
        // do nothing
    }
}
