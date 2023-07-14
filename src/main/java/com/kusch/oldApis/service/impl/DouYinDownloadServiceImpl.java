package com.kusch.oldApis.service.impl;

import com.kusch.oldApis.service.DownloadService;
import com.kusch.constants.CommonConstants;
import com.kusch.constants.PlatformConstants;
import com.kusch.ex.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抖音短视频下载
 *
 * @author Mr.kusch
 * @date 2022/11/24 15:07
 */
@Service
@Slf4j
public class DouYinDownloadServiceImpl implements DownloadService {

    /**
     * 抖音获取视频id的正则
     */
    private static final String DOU_YIN_ID = "\\d+";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void parse(String url, String way, HttpServletResponse response) {
        try {
            douyin(url, way, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage());
        }
    }

    private void douyin(String url, String way, HttpServletResponse response) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 获取重定向后的URL
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        String redirectUrl = forEntity.getHeaders().get(CommonConstants.REDIRECT_URI).get(0);

        // 获取视频ID
        Pattern pattern = Pattern.compile(DOU_YIN_ID);
        Matcher matcher = pattern.matcher(redirectUrl);
        String id;
        if (matcher.find()) {
            id = matcher.group();
        } else {
            matcher = pattern.matcher(url);
            if (matcher.find()) {
                id = matcher.group();
            } else {
                throw new ApiException("解析失败");
            }
        }
        // 发送POST请求获取下载地址
        JSONObject data = new JSONObject();
        data.put("url", "https://www.douyin.com/aweme/v1/web/aweme/detail/?aweme_id=" + id + "&aid=1128&version_name" +
                "=23.5.0&device_platform=android&os_version=2333");
        data.put("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/109.0.0.0 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(data.toString(), headers);
        ResponseEntity<String> responsex = restTemplate.postForEntity("http://49.235.29.74:19999/",
                entity,
                String.class);
        JSONObject jsonObject = new JSONObject(responsex.getBody());
        String videoUrl = jsonObject.getJSONObject("data").getString("url");

        // 获取视频下载链接
        String msToken = UUID.randomUUID().toString().replace("-", "");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/109.0.0.0 Safari/537.36");
        headers.set("Referer", "https://www.douyin.com/");
        headers.set("Cookie", "msToken=" + msToken + ";odin_tt" +
                "=324fb4ea4a89c0c05827e18a1ed9cf9bf8a17f7705fcc793fec935b637867e2a5a9b8168c885554d029919117a18ba69; " +
                "ttwid=1%7CWBuxH_bhbuTENNtACXoesI5QHV2Dt9-vkMGVHSRRbgY%7C1677118712" +
                "%7C1d87ba1ea2cdf05d80204aea2e1036451dae638e7765b8a4d59d87fa05dd39ff; " +
                "bd_ticket_guard_client_data" +
                "=eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWNsaWVudC1jc3IiOiItLS0tLUJFR0lOIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLVxyXG5NSUlCRFRDQnRRSUJBREFuTVFzd0NRWURWUVFHRXdKRFRqRVlNQllHQTFVRUF3d1BZbVJmZEdsamEyVjBYMmQxXHJcbllYSmtNRmt3RXdZSEtvWkl6ajBDQVFZSUtvWkl6ajBEQVFjRFFnQUVKUDZzbjNLRlFBNUROSEcyK2F4bXAwNG5cclxud1hBSTZDU1IyZW1sVUE5QTZ4aGQzbVlPUlI4NVRLZ2tXd1FJSmp3Nyszdnc0Z2NNRG5iOTRoS3MvSjFJc3FBc1xyXG5NQ29HQ1NxR1NJYjNEUUVKRGpFZE1Cc3dHUVlEVlIwUkJCSXdFSUlPZDNkM0xtUnZkWGxwYmk1amIyMHdDZ1lJXHJcbktvWkl6ajBFQXdJRFJ3QXdSQUlnVmJkWTI0c0RYS0c0S2h3WlBmOHpxVDRBU0ROamNUb2FFRi9MQnd2QS8xSUNcclxuSURiVmZCUk1PQVB5cWJkcytld1QwSDZqdDg1czZZTVNVZEo5Z2dmOWlmeTBcclxuLS0tLS1FTkQgQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tXHJcbiJ9;");
        HttpEntity<String> entity2 = new HttpEntity<>(null, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(videoUrl, HttpMethod.GET, entity2, String.class);

        JSONObject jsonObject1 = new JSONObject(response2.getBody());
        String toDownloadUrl = jsonObject1.getJSONObject("aweme_detail")
                .getJSONObject("video")
                .getJSONObject("play_addr")
                .getJSONArray("url_list")
                .get(0).toString();

        if (way.equals(PlatformConstants.DOWNLOAD_STREAM)) {
            ResponseEntity<byte[]> responseEntity
                    = restTemplate.getForEntity(toDownloadUrl, byte[].class);
            byte[] body = responseEntity.getBody();
            if (body != null && body.length > 0) {
                download(response, "抖音视频" + System.currentTimeMillis(), body);
            } else {
                log.warn("{}----响应体没有内容" + this.getClass().getName());
            }
        } else {
            response.setHeader("content-type", "text/html;charset=utf-8");
            response.getOutputStream().write(toDownloadUrl.getBytes());
        }
    }

    @Override
    public String platform() {
        return PlatformConstants.DOU_YIN;
    }
}
