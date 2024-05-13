package com.ndnhuy.toy.kvstore;

public interface KVLogger<K, V> {
    void writePut(K key, V value);

    void writeDelete(K key);
}