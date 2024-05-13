package com.ndnhuy.toy.kvstore.cluster;

import com.ndnhuy.toy.kvstore.Event;
import com.ndnhuy.toy.kvstore.pubsub.InmemoryQueue;
import com.ndnhuy.toy.kvstore.pubsub.PubSub;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a gossip cluster, every member will notify the change to every other members via pubsub
 */
public class GossipKVCluster implements KVCluster {

    private final ClusterManager clusterManager;

    public GossipKVCluster(PubSub pubSub) {
        clusterManager = new BroadcastClusterManager(pubSub);
    }

    @Override
    public ClusterManager getClusterManager() {
        return new ImmutableClusterManager(this.clusterManager);
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

    @Slf4j
    private static class BroadcastClusterManager implements ClusterManager {

        private PubSub pubsub = new InmemoryQueue();

        ClusterMember localMember;

        List<ClusterMember> externalMembers = new ArrayList<>();

        BroadcastClusterManager(PubSub pubsub) {
            this.pubsub = pubsub;
        }

        @Override
        public void replicate(Event event) {
            log.info("replicate: {}", event);
            pubsub.publish(event);
        }

        @Override
        public ClusterMember getLocalMember() {
            return new ImmutableClusterMember(this.localMember);
        }

        @Override
        public List<ClusterMember> getExternalMembers() {
            return this.externalMembers.stream().map(ImmutableClusterMember::new).collect(Collectors.toList());
        }

        @Override
        public void setLocalMember(ClusterMember member) {
            log.info("local member joined: {}", member.getId());
            this.localMember = member;
            pubsub.subscribe(event -> this.localMember.receive(event));
        }

        @Override
        public void addExternalMember(ClusterMember member) {
            log.info("external member joined: {}", member.getId());
            this.externalMembers.add(member);
        }

        @Override
        public void removeMember(ClusterMember member) {
            log.info("member left: {}", member.getId());
            if (member.getId().equals(localMember.getId())) {
                this.localMember = null;
            } else {
                this.externalMembers.remove(member);
            }
        }
    }
}
