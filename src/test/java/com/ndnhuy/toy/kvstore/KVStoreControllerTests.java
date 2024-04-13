package com.ndnhuy.toy.kvstore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class KVStoreControllerTests {

  @Autowired
  private MockMvc mockMvc;

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
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
