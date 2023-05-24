package com.kusch;

import com.kusch.ex.ApiException;
import com.kusch.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
