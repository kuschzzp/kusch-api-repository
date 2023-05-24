package com.kusch.apis.service.impl;

import com.kusch.apis.service.DownloadService;
import com.kusch.constants.CommonConstants;
import com.kusch.constants.PlatformConstants;
import com.kusch.constants.RequestConstants;
import com.kusch.ex.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
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
    private static final String KUAI_SHOU_ID = "photoId=(.*?)\\&";

    /**
     * 快手视频正则
     */
    private static final String KUAI_SHOU_URL = "short-video\\/(.*?)\\?";

    @Override
    public void parse(String url, String way, HttpServletResponse response) {

        try {
            kuaishou(url, way, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage());
        }

    }

    private void kuaishou(String url, String way, HttpServletResponse response) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", "did=web_edbadd8e37094e728293c216fdf9f3a7; didv=" + System.currentTimeMillis() + ";" +
                "kpf=PC_WEB; kpn=KUAISHOU_VISION");
        headers.set("Referer", url);
        headers.set("User-Agent", RequestConstants.MAC_USER_AGENT);

        String videoId = "";
        if (url.contains("v.kuaishou.com")) {
            // 获取重定向后的URL
            ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
            String redirectUrl = forEntity.getHeaders().get(CommonConstants.REDIRECT_URI).get(0);

            Pattern pattern = Pattern.compile(KUAI_SHOU_ID);
            Matcher matcher = pattern.matcher(redirectUrl);
            matcher.find();
            videoId = matcher.group(1);
        } else {
            Pattern pattern = Pattern.compile(KUAI_SHOU_URL);
            Matcher matcher = pattern.matcher(url);
            matcher.find();
            videoId = matcher.group(1);
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("photoId", videoId);
        requestBody.put("isLongVideo", false);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<String> entity = restTemplate.postForEntity("https://v.m.chenzhongtech" +
                ".com/rest/wd/photo/info", requestEntity, String.class);
        JSONObject responseJson = new JSONObject(entity.getBody());
        JSONObject photo = responseJson.getJSONObject("photo");
        String videoUrl = photo.getJSONArray("mainMvUrls").getJSONObject(0).getString("url");

        if (way.equals(PlatformConstants.DOWNLOAD_STREAM)) {
            ResponseEntity<byte[]> responseEntity
                    = restTemplate.getForEntity(videoUrl, byte[].class);
            byte[] body = responseEntity.getBody();
            if (body != null && body.length > 0) {
                download(response, "快手视频", body);
            } else {
                log.warn("{}----响应体没有内容" + this.getClass().getName());
            }
        } else {
            response.setHeader("content-type", "text/html;charset=utf-8");
            response.getOutputStream().write(videoUrl.getBytes());
        }
    }

    @Override
    public String platform() {
        return PlatformConstants.KUAI_SHOU;
    }
}
