package com.kusch.service.impl;

import com.kusch.constants.CommonConstants;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 皮皮虾短视频下载
 *
 * @author Mr.kusch
 * @date 2022/11/24 15:07
 */
@Service
@Slf4j
public class PiPiXiaDownloadServiceImpl implements DownloadService {

    /**
     * 获取真实皮皮虾ID的正则
     */
    private static final String PI_PI_XIA_ID = "em/(.*)[/]?\\?";

    /**
     * 详情获取请求
     */
    private static final String PI_PI_XIA_INFO_URL = "https://is.snssdk.com/bds/cell/detail/?cell_type=1&aid=1319" +
            "&app_name=super&cell_id=";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void parse(String url, String way, HttpServletResponse response) {

        try {
            pipixia(url, way, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    private void pipixia(String url, String way, HttpServletResponse response) throws IOException {
        //第一步还是获取ID
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        HttpHeaders headers = forEntity.getHeaders();
        List<String> list = headers.get(CommonConstants.REDIRECT_URI);
        if (!CollectionUtils.isEmpty(list)) {
            String locationUrl = list.get(0);
            Pattern pt = Pattern.compile(PI_PI_XIA_ID);
            Matcher matcher = pt.matcher(locationUrl);
            String id = "";
            while (matcher.find()) {
                id = matcher.group(1);
            }
            Assert.isTrue(StringUtils.isNotBlank(id), "正则匹配出错！");
            ResponseEntity<String> entity = restTemplate.getForEntity(PI_PI_XIA_INFO_URL + id,
                    String.class);
            //处理返回信息中的unicode编码
            String decode = CommonUtils.unicodeDecode(entity.getBody());
            Map<String, Object> stringObjectMap = JsonUtils.jsonToMap(decode);
            //根据层级获取真实地址
            HashMap<String, Object> data = (HashMap<String, Object>) stringObjectMap.get("data");
            HashMap<String, Object> sonData = (HashMap<String, Object>) data.get("data");
            HashMap<String, Object> itemData = (HashMap<String, Object>) sonData.get("item");
            HashMap<String, Object> vedios = (HashMap<String, Object>) itemData.get("origin_video_download");
            List<HashMap<String, Object>> vediosUrls = (List<HashMap<String, Object>>) vedios.get("url_list");
            String toDownloadUrl = (String) vediosUrls.get(0).get("url");

            if (way.equals(PlatformConstants.DOWNLOAD_STREAM)) {
                ResponseEntity<byte[]> responseEntity
                        = restTemplate.getForEntity(toDownloadUrl, byte[].class);
                String filename = (String) itemData.get("content");
                filename = StringUtils.isBlank(filename) ? "pipixia" : filename;
                byte[] body = responseEntity.getBody();
                if (body != null && body.length > 0) {
                    download(response, filename, body);
                } else {
                    log.warn("{}----响应体没有内容" + this.getClass().getName());
                }
            } else {
                response.setHeader("content-type", "text/html;charset=utf-8");
                response.getWriter().write(toDownloadUrl);
            }
        } else {
            log.warn("请求返回结果中没有location！！");
        }
    }

    @Override
    public String platform() {
        return PlatformConstants.PI_PI_XIA;
    }
}
