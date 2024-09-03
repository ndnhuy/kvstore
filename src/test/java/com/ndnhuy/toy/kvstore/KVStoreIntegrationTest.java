package com.ndnhuy.toy.kvstore;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class KVStoreIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testPut_shouldPublishToRabbitMQ() {
        var k = UUID.randomUUID().toString();
        var v = UUID.randomUUID().toString();
        mockMvc.perform(
                        post("/kvstore/" + k)
                                .contentType("application/json")
                                .content(v))
                .andDo(print())
                .andExpect(status().isCreated());

        mockMvc.perform(get("/kvstore/" + k))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(v));
    }

}
