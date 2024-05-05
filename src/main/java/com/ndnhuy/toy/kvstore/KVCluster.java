package com.ndnhuy.toy.kvstore;

import com.ndnhuy.toy.kvstore.cluster.ClusterMember;

public interface KVCluster {
    void registerLocalMember(ClusterMember member);

    void registerExternalMember(ClusterMember member);

    void remove(ClusterMember member);
}
