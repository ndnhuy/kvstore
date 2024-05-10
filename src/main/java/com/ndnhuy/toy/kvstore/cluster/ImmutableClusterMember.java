package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ImmutableClusterMember implements ClusterMember {

    private ClusterMember clusterMember;

    @Override
    public String getId() {
        return clusterMember.getId();
    }

    @Override
    public void accept(Replicator replicator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void leave() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void receive(Event event) {
        throw new UnsupportedOperationException();
    }
}
