package com.ndnhuy.toy.kvstore;

import com.ndnhuy.toy.kvstore.cluster.GossipKVCluster;
import com.ndnhuy.toy.kvstore.cluster.KVCluster;
import com.ndnhuy.toy.kvstore.pubsub.BroadcastPubSub;
import com.ndnhuy.toy.kvstore.pubsub.PubSub;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KVStoreConfig {

    @Bean
    public KVStore<String, String> kvStore(KVCluster cluster) {
        var commitLog = new InmemoryCommitLog();
        var kvStore = new SimpleKVStore("test", commitLog);
        cluster.registerLocalMember(kvStore);
        return kvStore;
    }

    @Bean
    public KVCluster kvCluster(PubSub pubSub) {
        return new GossipKVCluster(pubSub);
    }

    @Bean
    public PubSub pubSub(RabbitTemplate rabbitTemplate) {
        return new BroadcastPubSub(rabbitTemplate);
    }
}
