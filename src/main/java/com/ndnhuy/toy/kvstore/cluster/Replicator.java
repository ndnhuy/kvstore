package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.Event;

public interface Replicator {
    void replicate(Event event);
}
