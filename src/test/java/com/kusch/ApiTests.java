package com.kusch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
@Slf4j
class ApiTests {

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void test() throws Exception {
        douyin("https://v.douyin.com/hWVVhYe/");
    }

    public void douyin(String url) throws JSONException {


    }

}
