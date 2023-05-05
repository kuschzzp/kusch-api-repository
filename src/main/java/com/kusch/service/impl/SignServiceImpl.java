package com.kusch.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.kusch.service.SignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 签到的实现类
 *
 * @author Mr.Kusch
 * @date 2023年05月05日 15:44
 */
@Service
@Slf4j
public class SignServiceImpl implements SignService {

    /**
     * 固定参数 1, 这个参数有点像用户ID
     */
    private static final String USER_ID = "131661";
    /**
     * 固定参数 2
     */
    private static final String KEY = "qd396qsy1110";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void hengHengMaoSign() {
        long time = System.currentTimeMillis();

        String t = time + "";
        String u = MD5.create().digestHex((USER_ID + KEY + t));

        String url = "https://video.baoge.vip/user/ajax.php?act=qiandao&t=" + t + "&s=" + u;
        String cookie = "mysid=2f6e27d466094af520067d89b7a6e330; _ga=GA1.1.303546372.1683119313; " +
                "user_token" +
                "=1c69MDAwMDAwMDAwMGI4OGZkYzgwNmNlZWEyYjgxMzE2NjEJZGFkNGQyMjJiMWQxMDVhODE3YzY4OWE1OTFjZjViMjU; " +
                "PHPSESSID=tapct10kbvrs5vbet0q1cmh957; _ga_NVM5R0RWJE=GS1.1.1683258383.2.0.1683258383.0.0.0";

        HttpHeaders httpHeaders  =new HttpHeaders();
        httpHeaders.add("Cookie",cookie);
        httpHeaders.add("Host","video.baoge.vip");
        httpHeaders.add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders),
                String.class);

        log.info(exchange.getBody());

    }
}
