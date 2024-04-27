package com.ndnhuy.toy.kvstore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SimpleKVStore implements KVStore<String, String> {

  private Map<String, String> store = new ConcurrentHashMap<>();

  @Override
  public String get(String key) {
    var v = store.get(key);
    log.info("Get [{},{}]", key, v);
    return v;
  }

  @Override
  public void put(String key, String value) {
    log.info("Put [{},{}]", key, value);
    store.put(key, value);
  }

  @Override
  public void delete(String key) {
    log.info("Delete key {}", key);
    store.remove(key);
  }

}
