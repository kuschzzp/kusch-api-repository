package com.kusch.apis.service.impl;

import com.kusch.apis.service.DownloadService;
import com.kusch.constants.PlatformConstants;
import com.kusch.ex.ApiException;
import com.kusch.result.R;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 最右视频下载
 *
 * @author Mr.kusch
 * @date 2022/11/26 17:15
 */
@Service
@Slf4j
public class ZuiYouDownloadServiceImpl implements DownloadService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void parse(String url, String way, HttpServletResponse response) {

        try {
            zuiyou(url, way, response);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage());
        }

    }

    private void zuiyou(String url, String way, HttpServletResponse response) throws IOException {
        ResponseEntity<String> entity = restTemplate.postForEntity(url, new LinkedMultiValueMap<>(), String.class);
        Document document = Jsoup.parse(Objects.requireNonNull(entity.getBody()));
        String filename = document.getElementsByTag("title").text().replace(" - 最右", "");
        Elements attributeValue = document.getElementsByAttributeValue("preload", "metadata");
        String toDownloadUrl = attributeValue.get(0).attr("src");

        if (way.equals(PlatformConstants.DOWNLOAD_STREAM)) {
            ResponseEntity<byte[]> responseEntity
                    = restTemplate.getForEntity(toDownloadUrl, byte[].class);
            byte[] body = responseEntity.getBody();
            if (body != null && body.length > 0) {
                download(response, filename, body);
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
        return PlatformConstants.ZUI_YOU;
    }
}
