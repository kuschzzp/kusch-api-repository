package com.kusch.service.impl;

import com.kusch.config.GetUriRedirectStrategy;
import com.kusch.constants.PlatformConstants;
import com.kusch.service.DownloadService;
import com.kusch.utils.CommonUtils;
import com.kusch.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    private static final String DOU_YIN_ID = "o/(.*)[/]?\\?";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void parse(String url, HttpServletResponse response) {
        try {
            douyin(url, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void douyin(String url, HttpServletResponse response) throws IOException {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        HttpHeaders headers = forEntity.getHeaders();
        List<String> list = headers.get(GetUriRedirectStrategy.REDIRECT_URI);
        if (!CollectionUtils.isEmpty(list)) {
            String locationUrl = list.get(0);
            Pattern pt = Pattern.compile(DOU_YIN_ID);
            Matcher matcher = pt.matcher(locationUrl);
            String id = "";
            while (matcher.find()) {
                id = matcher.group(1);
            }
            Assert.isTrue(StringUtils.isNotBlank(id), "正则匹配出错！");
            ResponseEntity<String> entity = restTemplate.getForEntity("https://www.iesdouyin" +
                            ".com/web/api/v2/aweme/iteminfo/?item_ids=" + id,
                    String.class);
            //处理返回信息中的unicode编码
            String decode = CommonUtils.unicodeDecode(entity.getBody());
            Map<String, Object> stringObjectMap = JsonUtils.jsonToMap(decode);
            //根据层级获取真实地址
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) stringObjectMap.get("item_list");
            Map<String, Object> map = (Map<String, Object>) itemList.get(0).get("video");
            Map<String, Object> playAddr = (Map<String, Object>) map.get("play_addr");
            List<String> urlList = (List<String>) playAddr.get("url_list");
            String toDownloadUrl = urlList.get(0).replaceFirst("playwm", "play");

            ResponseEntity<byte[]> responseEntity
                    = restTemplate.getForEntity(toDownloadUrl, byte[].class);
            String filename = (String) itemList.get(0).get("desc");
            byte[] body = responseEntity.getBody();
            if (body != null && body.length > 0) {
                download(response, filename, body);
            } else {
                log.warn("{}----响应体没有内容" + this.getClass().getName());
            }
        } else {
            log.warn("请求返回结果中没有location！！");
        }
    }

    @Override
    public String platform() {
        return PlatformConstants.DOU_YIN;
    }
}
