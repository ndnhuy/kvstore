package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.Event;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ImmutableClusterManager implements ClusterManager {

    private ClusterManager clusterManager;

    @Override
    public ClusterMember getLocalMember() {
        return clusterManager.getLocalMember();
    }

    @Override
    public List<ClusterMember> getExternalMembers() {
        return clusterManager.getExternalMembers();
    }

    @Override
    public void setLocalMember(ClusterMember member) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addExternalMember(ClusterMember member) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeMember(ClusterMember member) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replicate(Event event) {
        throw new UnsupportedOperationException();
    }
}
