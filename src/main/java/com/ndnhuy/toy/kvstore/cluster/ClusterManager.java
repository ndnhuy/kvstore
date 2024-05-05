package com.ndnhuy.toy.kvstore.cluster;

public interface ClusterManager extends Replicator {

    void setLocalMember(ClusterMember member);

    void addExternalMember(ClusterMember member);

    void removeMember(ClusterMember member);
}
