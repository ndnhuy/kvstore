package com.ndnhuy.toy.kvstore;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SimpleKVStoreTests {

  private SimpleKVStore kvStore = new SimpleKVStore();

  @Test
  void testStore() {
    kvStore.put("k1", "v1");
    assertThat(kvStore.get("k1")).isEqualTo("v1");
    kvStore.delete("k1");
    assertThat(kvStore.get("k1")).isNull();
  }

}
