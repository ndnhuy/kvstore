package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.Event;

public class NoopReplicator implements Replicator {
    @Override
    public void replicate(Event event) {
        // do nothing
    }
}
