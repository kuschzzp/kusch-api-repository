package com.kusch.apis.controller;

import com.kusch.apis.handler.DownloadHandler;
import com.kusch.apis.service.ClashNodeService;
import com.kusch.constants.PlatformConstants;
import com.kusch.utils.IpUtil;
import com.kusch.utils.RegxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对外API
 *
 * @author Mr.kusch
 * @date 2022年11月21日 15:50
 */
@RestController
@CrossOrigin
@Slf4j
public class ApiController {

    @Autowired
    private DownloadHandler handler;

    @Autowired
    private ClashNodeService clashNodeService;

    /**
     * 视频下载的统一入口<br/>
     * 例如：127.0.0.1:17777/video?url=https://v.kuaishou.com/CHwRjS&way=0
     *
     * @param url      视频url
     * @param way      下载方式   0 流下载  1 直接返回URL
     * @param response 响应流
     */
    @GetMapping("/video")
    public void getInfo(@RequestParam String url,
                        @RequestParam String way,
                        HttpServletResponse response) {
        String realUrl = RegxUtils.getRealUrl(url);
        log.info("解析出得URL：{}", realUrl);
        handler.download(PlatformConstants.getRealPlatform(realUrl), realUrl, way, response);
    }

    /**
     * 获取免费的机场节点 yaml格式可以直接把链接填入 clash
     *
     * @param type 类型： txt/yaml
     */
    @GetMapping("/freeNode")
    public void clashNode(String type,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        clashNodeService.getTaregtClashNodeService(type, request, response);
    }


    /**
     * 获取真实ip地址,不返回内网地址
     */
    @GetMapping("/ip")
    public String getIpAddr(HttpServletRequest request) {
        String ipAddr = IpUtil.getIpAddr(request);
        log.info("本次请求的IP是: {}", ipAddr);
        return ipAddr;
    }
}
