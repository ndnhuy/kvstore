package com.ndnhuy.toy.kvstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndnhuy.toy.kvstore.pubsub.PubSub;
import com.ndnhuy.toy.kvstore.rabbitmq.MessageReceiver;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class KVStoreControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PubSub pubSub;

    @Autowired
    private MessageReceiver messageReceiver;

    @Test
    void testKVStore() throws Exception {
        mockMvc.perform(
                        post("/kvstore/key1")
                                .contentType("application/json")
                                .content("value1"))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/kvstore/key1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("value1"));

        mockMvc.perform(delete("/kvstore/key1"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/kvstore/key1"))
                .andDo(print())
                .andExpect(status().isNotFound());

        var decoratorPubSub = (TestConfiguration.DecoratorPubSub) pubSub;
        assertThat(decoratorPubSub.getPublishedEvents().size()).isEqualTo(2);
        var putEvent = decoratorPubSub.getPublishedEvents().get(0);
        assertThat(putEvent.getType()).isEqualTo(EventType.PUT);
        assertThat(putEvent.getKey()).isEqualTo("key1");
        assertThat(putEvent.getValue()).isEqualTo("value1");
        var deleteEvent = decoratorPubSub.getPublishedEvents().get(1);
        assertThat(deleteEvent.getType()).isEqualTo(EventType.DELETE);
        assertThat(deleteEvent.getKey()).isEqualTo("key1");

        var decoratorMessageReceiver = (TestConfiguration.DecoratorMessageReceiver) messageReceiver;
        assertThat(decoratorMessageReceiver.receivedMessages.size()).isEqualTo(2);
        var putMsg = decoratorMessageReceiver.receivedMessages.get(0);
        assertEquals(putEvent, asEventObject(putMsg));
        var deleteMsg = decoratorMessageReceiver.receivedMessages.get(1);
        assertEquals(deleteEvent, asEventObject(deleteMsg));
    }

    private void assertEquals(Event a, Event b) {
        assertThat(a.getKey()).isEqualTo(b.getKey());
        assertThat(a.getValue()).isEqualTo(b.getValue());
        assertThat(a.getType()).isEqualTo(b.getType());
    }

    private Event asEventObject(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Event.class);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {

        @Bean
        @Qualifier("pubSub")
        @Primary
        public PubSub mockPubSub(PubSub pubSub) {
            return new DecoratorPubSub(pubSub);
        }

        @Bean
        @Qualifier("messageReceiver")
        @Primary
        public MessageReceiver mockMessageReceiver(MessageReceiver messageReceiver) {
            return new DecoratorMessageReceiver(messageReceiver);
        }

        static class DecoratorMessageReceiver implements MessageReceiver {

            final List<String> receivedMessages = new ArrayList<>();

            final MessageReceiver delegate;

            DecoratorMessageReceiver(MessageReceiver messageReceiver) {
                this.delegate = messageReceiver;
            }

            @Override
            public void receiveMessage(String message) {
                receivedMessages.add(message);
                delegate.receiveMessage(message);
            }
        }

        @Getter
        static class DecoratorPubSub implements PubSub {

            private final List<Event> publishedEvents = new ArrayList<>();

            private final PubSub delegate;

            DecoratorPubSub(PubSub pubSub) {
                delegate = pubSub;
            }

            @Override
            public void publish(Event event) {
                publishedEvents.add(event);
                delegate.publish(event);
            }

            @Override
            public void subscribe(Consumer<Event> eventHandler) {
            }

            @Override
            public void start() {
            }

            @Override
            public void shutdown() {
            }

        }
    }
}
