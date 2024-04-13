package com.ndnhuy.toy.kvstore;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/kvstore")
public class KVStoreController {

  private KVStore<String, String> kvstore;

  @GetMapping("/{key}")
  public ResponseEntity<String> get(@PathVariable String key) {
    var v = kvstore.get(key);
    if (v != null) {
      return ResponseEntity.ok(v);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/{key}")
  public ResponseEntity<Void> put(@PathVariable String key, @RequestBody String value) {
    kvstore.put(key, value);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @DeleteMapping("/{key}")
  public ResponseEntity<Void> put(@PathVariable String key) {
    kvstore.delete(key);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
