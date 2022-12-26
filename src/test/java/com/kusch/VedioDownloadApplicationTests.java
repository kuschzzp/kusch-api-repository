package com.kusch;

import com.kusch.config.GetUriRedirectStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class VedioDownloadApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {

        // 网页端 https://www.bilibili.com/video/BV1BG411P76M/?share_source=copy_web&vd_source=1b5bc68dd777d71f2e05f2831022908d
        // 手机端 https://b23.tv/giA4GNh
        ResponseEntity<String> forEntity = restTemplate.getForEntity("https://b23.tv/giA4GNh", String.class);
        System.out.println(forEntity.getBody());

        System.out.println(forEntity.getHeaders().get(GetUriRedirectStrategy.REDIRECT_URI));
    }

}
