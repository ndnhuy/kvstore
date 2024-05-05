package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.Event;
import com.ndnhuy.toy.kvstore.KVCluster;
import com.ndnhuy.toy.kvstore.pubsub.InmemoryQueue;
import com.ndnhuy.toy.kvstore.pubsub.PubSub;

import java.util.ArrayList;
import java.util.List;

public class SimpleKVCluster implements KVCluster {


    private final ClusterManager clusterManager;

    public SimpleKVCluster(PubSub pubSub) {
        clusterManager = new BroadcastClusterManager(pubSub);
    }

    @Override
    public void registerLocalMember(ClusterMember member) {
        member.accept(clusterManager);
        clusterManager.setLocalMember(member);
    }

    @Override
    public void registerExternalMember(ClusterMember member) {
        member.accept(clusterManager);
        clusterManager.addExternalMember(member);
    }

    @Override
    public void remove(ClusterMember member) {
        member.leave();
        clusterManager.removeMember(member);
    }

    private static class BroadcastClusterManager implements ClusterManager {

        private PubSub pubsub = new InmemoryQueue();

        ClusterMember localMember;

        List<ClusterMember> externalMembers = new ArrayList<>();

        BroadcastClusterManager(PubSub pubsub) {
            this.pubsub = pubsub;
        }

        @Override
        public void replicate(Event event) {
            // publish events to message broker to replicate the change to other members in same cluster
            pubsub.publish(event);
            pubsub.publish(event);
        }

        @Override
        public void setLocalMember(ClusterMember member) {
            this.localMember = member;
            pubsub.subscribe(event -> this.localMember.notify(event));
        }

        @Override
        public void addExternalMember(ClusterMember member) {
            this.externalMembers.add(member);
        }

        @Override
        public void removeMember(ClusterMember member) {
            if (member.getId().equals(localMember.getId())) {
                this.localMember = null;
            } else {
                this.externalMembers.remove(member);
            }
        }
    }
}
