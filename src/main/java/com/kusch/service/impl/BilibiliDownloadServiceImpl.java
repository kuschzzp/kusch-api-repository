package com.kusch.service.impl;

import com.kusch.constants.CommonConstants;
import com.kusch.constants.PlatformConstants;
import com.kusch.service.DownloadService;
import com.kusch.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 哔哩哔哩视频下载
 *
 * @author Mr.kusch
 * @date 2022/12/26 11:07
 */
@Service
@Slf4j
public class BilibiliDownloadServiceImpl implements DownloadService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void parse(String url, String way, HttpServletResponse response) {

        try {
            bilibili(url, way, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    private void bilibili(String url, String way, HttpServletResponse response) throws IOException {
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
        Document document = Jsoup.parse(Objects.requireNonNull(entity.getBody()));
        String filename = document.getElementsByTag("title").text().replace("_哔哩哔哩_bilibili", "");
        Elements script = document.getElementsByTag("script");
        String infoJson = "";
        for (Element element : script) {
            List<Node> nodes = element.childNodes();
            if (!CollectionUtils.isEmpty(nodes) && nodes.get(0).toString().contains("__playinfo__")) {
                infoJson = nodes.get(0).toString().replace("window.__playinfo__=", "");
                break;
            }
        }
        Map<String, Object> map = JsonUtils.jsonToMap(infoJson);
        Map<String, Object> data = (Map<String, Object>) map.get("data");
        Map<String, Object> dash = (Map<String, Object>) data.get("dash");
        List<Map<String, Object>> video = (List<Map<String, Object>>) dash.get("video");
        Map<String, Object> videoIndo = video.get(0);
        String toDownloadUrl = (String) videoIndo.get("baseUrl");

        if (way.equals(PlatformConstants.DOWNLOAD_STREAM)) {

            //这个比较特殊，需要携带 referer 请求头
            String referer = "";
            if (url.contains(PlatformConstants.BILIBILI_PHONE)) {
                referer =
                        "https://www.bilibili.com" + entity.getHeaders().get(CommonConstants.REDIRECT_URI).get(0);
            } else {
                referer = url;
            }

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("referer", referer);

            ResponseEntity<byte[]> responseEntity
                    = restTemplate.exchange(toDownloadUrl, HttpMethod.GET, new HttpEntity<>(httpHeaders), byte[].class);

            byte[] body = responseEntity.getBody();
            if (body != null && body.length > 0) {
                download(response, filename, body);
            } else {
                log.warn("{}----响应体没有内容" + this.getClass().getName());
            }
        } else {
            response.setHeader("content-type", "text/html;charset=utf-8");
            response.getWriter().write("该站点不支持使用URL方式！不信你试试：  " + toDownloadUrl);
        }
    }

    @Override
    public String platform() {
        return PlatformConstants.BILIBILI;
    }
}
