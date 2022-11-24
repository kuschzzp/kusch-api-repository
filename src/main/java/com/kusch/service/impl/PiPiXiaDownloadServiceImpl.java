package com.kusch.service.impl;

import com.kusch.constants.PlatformConstants;
import com.kusch.service.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

/**
 * 皮皮虾短视频下载
 *
 * @author Mr.kusch
 * @date 2022/11/24 15:07
 */
@Service
@Slf4j
public class PiPiXiaDownloadServiceImpl implements DownloadService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void parse(String url, HttpServletResponse response) {

        //TODO:.....

    }


    @Override
    public String platform() {
        return PlatformConstants.PI_PI_XIA;
    }
}
