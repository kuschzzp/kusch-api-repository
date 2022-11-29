package com.kusch.service.impl;

import com.kusch.config.GetUriRedirectStrategy;
import com.kusch.constants.PlatformConstants;
import com.kusch.service.DownloadService;
import com.kusch.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 快手视频下载
 *
 * @author Mr.kusch
 * @date 2022/11/29 22:02
 */
@Service
@Slf4j
public class KuaiShouDownloadServiceImpl implements DownloadService {

    @Autowired
    private RestTemplate restTemplate;
    /**
     * 快手获取视频id的正则
     */
    private static final String KUAI_SHOU_ID = "oto/(.*)\\?f";

    /**
     * 快手视频正则
     */
    private static final String KUAI_SHOU_URL = "\"photoUrl\":\"(.*)\",\"lik";

    @Override
    public void parse(String url, HttpServletResponse response) {

        try {
            kuaishou(url, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void kuaishou(String url, HttpServletResponse response) throws IOException {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        HttpHeaders headers = forEntity.getHeaders();
        List<String> list = headers.get(GetUriRedirectStrategy.REDIRECT_URI);
        if (!CollectionUtils.isEmpty(list)) {
            String locationUrl = list.get(0);
            Pattern pt = Pattern.compile(KUAI_SHOU_ID);
            Matcher matcher = pt.matcher(locationUrl);
            String id = "";
            while (matcher.find()) {
                id = matcher.group(1);
            }
            Assert.isTrue(StringUtils.isNotBlank(id), "正则匹配出错！");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Cookie", "clientid=3; did=web_48e73bf3f7a9444391f356155b16f021; didv=1669731954000; kpf=PC_WEB; kpn=KUAISHOU_VISION");
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(httpHeaders);
            ResponseEntity<String> entity = restTemplate.exchange("https://www.kuaishou.com/short-video/" + id,
                    HttpMethod.GET, httpEntity, String.class);
            //处理返回信息中的unicode编码
            String decode = CommonUtils.unicodeDecode(entity.getBody());

            Pattern pt1 = Pattern.compile(KUAI_SHOU_URL);
            Matcher matcher1 = pt1.matcher(decode);
            String toDownloadUrl = "";
            while (matcher1.find()) {
                toDownloadUrl = matcher1.group(1);
            }
            ResponseEntity<byte[]> responseEntity
                    = restTemplate.getForEntity(toDownloadUrl, byte[].class);
            byte[] body = responseEntity.getBody();
            if (body != null && body.length > 0) {
                download(response, "快手视频", body);
            } else {
                log.warn("{}----响应体没有内容" + this.getClass().getName());
            }
        } else {
            log.warn("请求返回结果中没有location！！");
        }
    }

    @Override
    public String platform() {
        return PlatformConstants.KUAI_SHOU;
    }
}
