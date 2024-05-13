package com.ndnhuy.toy.kvstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/kvstore")
public class KVStoreController {

    @Autowired
    private KVStore<String, String> kvStore;

    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        var v = kvStore.get(key);
        if (v != null) {
            return ResponseEntity.ok(v);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{key}")
    public ResponseEntity<Void> put(@PathVariable String key, @RequestBody String value) {
        kvStore.put(key, value);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> put(@PathVariable String key) {
        kvStore.delete(key);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
