package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.Event;

public interface ClusterMember {

    String getId();

    void accept(Replicator replicator);

    void leave();

    void receive(Event event);
}
