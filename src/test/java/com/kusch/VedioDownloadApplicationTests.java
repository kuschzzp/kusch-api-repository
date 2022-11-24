package com.kusch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class VedioDownloadApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    // https://v.douyin.com/r9MR5nt/
    @Test
    void contextLoads() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //        ResponseEntity<String> forEntity = restTemplate.getForEntity("https://v.douyin.com/r9MR5nt/",
        //                String.class);
        //        HttpHeaders headers = forEntity.getHeaders();
        //        for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
        //            System.out.println(stringListEntry.getKey() + "  ----  " + stringListEntry.getValue());
        //        }

        RequestEntity requestEntity =
                RequestEntity.get("https://v.douyin.com/r9MR5nt/")
                        // 添加 header
                        .header("cookie", "ttwid=1%7CeCEhGGOTmhWWiaAL8qIJSQqfyxPdEwMU_Om4xs6YOYk%7C1660374859" +
                                "%7Cb951c289049610295dfcdf1d29b9997b31d9d26df15bcfd9fcb83148aa8744e3; " +
                                "d_ticket=dee69064f62bf3af5305b593c376c187c8e7b; " +
                                "n_mh=A046Zc7ymtegINYK8-FvSIcGbO0ICYsCNca0DbnvTMc; " +
                                "SEARCH_RESULT_LIST_TYPE=%22single%22; home_can_add_dy_2_desktop=%220%22; " +
                                "passport_csrf_token=fbc55dff0b2e35749f8494103967c031; " +
                                "passport_csrf_token_default=fbc55dff0b2e35749f8494103967c031; " +
                                "msToken=LhKZGrCneKrt6oye9gSt6PmaCif1gEfLfIheC" +
                                "-Tfz0DZ81K5IYjVNYfqnclmt4TCqJBwdR_8ErJYbTWHQyEp5_kYYx4gLj8iJUhPz8SGbidaibOHtipozDXAGGcs_wFm; strategyABtestKey=%221669110886.264%22")
                        .header("User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, likeGecko) " +
                                        "Chrome/79.0.3945.88 Safari/537.36")
                        .header("sec-ch-ua-platform", "Windows")
                        .accept(MediaType.valueOf(MediaType.ALL_VALUE))
                        .acceptCharset(StandardCharsets.UTF_8)
                        .build();
        ResponseEntity<Object> responseEntity = restTemplate.exchange(requestEntity, Object.class);
        System.out.println(responseEntity.getStatusCodeValue());
        HttpHeaders headers = responseEntity.getHeaders();
        for (Map.Entry<String, List<String>> stringListEntry : headers.entrySet()) {
            System.out.println(stringListEntry.getKey() + "  ----  " + stringListEntry.getValue());
        }
    }

}
