package com.ndnhuy.toy.kvstore.cluster;

import java.util.List;

public interface ClusterManager extends Replicator {

    ClusterMember getLocalMember();

    List<ClusterMember> getExternalMembers();

    void setLocalMember(ClusterMember member);

    void addExternalMember(ClusterMember member);

    void removeMember(ClusterMember member);
}
