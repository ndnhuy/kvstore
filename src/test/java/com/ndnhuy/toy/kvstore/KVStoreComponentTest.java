package com.ndnhuy.toy.kvstore;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KVStoreComponentTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    void testPutGetKeyValue() {
        doTestPutGetKeyValueOfInstance(8001);
        doTestPutGetKeyValueOfInstance(8002);
    }

    void doTestPutGetKeyValueOfInstance(int port) {
        var key = UUID.randomUUID().toString();
        var value = UUID.randomUUID().toString();
        var url = String.format("http://localhost:%d/kvstore/%s", port, key);
        var request = new HttpEntity<>(value);
        var response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(value);
    }

}
