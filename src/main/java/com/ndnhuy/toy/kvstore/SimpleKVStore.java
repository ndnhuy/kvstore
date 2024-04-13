package com.ndnhuy.toy.kvstore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class SimpleKVStore implements KVStore<String, String> {

  private Map<String, String> store = new ConcurrentHashMap<>();

  @Override
  public String get(String key) {
    return store.get(key);
  }

  @Override
  public void put(String key, String value) {
    store.put(key, value);
  }

  @Override
  public void delete(String key) {
    store.remove(key);
  }

}
