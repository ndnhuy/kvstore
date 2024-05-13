package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.cluster.ClusterManager;
import com.ndnhuy.toy.kvstore.cluster.ClusterMember;

public interface KVCluster {

    ClusterManager getClusterManager();

    void registerLocalMember(ClusterMember member);

    void registerExternalMember(ClusterMember member);

    void remove(ClusterMember member);
}
