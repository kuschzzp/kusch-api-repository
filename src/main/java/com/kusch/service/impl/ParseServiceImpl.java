package com.kusch.service.impl;

import com.kusch.config.GetUriRedirectStrategy;
import com.kusch.service.ParseService;
import com.kusch.utils.CommonUtils;
import com.kusch.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理
 *
 * @author Mr.kusch
 * @date 2022年11月21日 15:53
 */
@Service
@Slf4j
public class ParseServiceImpl implements ParseService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void parse(String url, HttpServletResponse response) {
        try {
            if (url.contains("douyin")) {
                // http://localhost:17777/download/common?url=https://v.douyin.com/r9MR5nt/
                douyin(url, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String DOU_YIN_ID = "o/(.*)[/]?\\?";

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
            List<Map<String, Object>> item_list = (List<Map<String, Object>>) stringObjectMap.get("item_list");
            Map<String, Object> map = (Map<String, Object>) item_list.get(0).get("video");
            Map<String, Object> play_addr = (Map<String, Object>) map.get("play_addr");
            List<String> url_list = (List<String>) play_addr.get("url_list");
            String toDownloadUrl = url_list.get(0).replaceFirst("playwm", "play");
            ResponseEntity<byte[]> responseEntity
                    = restTemplate.getForEntity(toDownloadUrl, byte[].class);
            byte[] body = responseEntity.getBody();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            String filename = (String) item_list.get(0).get("desc");
            // 此处的编码是文件名的编码，使能正确显示中文文件名
            try {
                response.setHeader("Content-Disposition",
                        "attachment;fileName=" + filename
                                + ";filename*=utf-8''" +
                                URLEncoder.encode(filename, "utf-8") + ".mp4");
                response.addHeader("realname", URLEncoder.encode(filename, "utf-8") + ".mp4");
            } catch (UnsupportedEncodingException e) {
                log.warn("文件名编码格式错误！");
                e.printStackTrace();
            }
            IOUtils.copy(byteArrayInputStream, response.getOutputStream());
        } else {
            log.warn("请求返回结果中没有location！！");
        }
    }
}
