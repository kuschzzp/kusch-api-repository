package com.kusch.controller;

import com.kusch.constants.PlatformConstants;
import com.kusch.handler.DownloadHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 对外接口
 *
 * @author Mr.kusch
 * @date 2022年11月21日 15:50
 */
@RestController
@RequestMapping("/download")
@CrossOrigin
public class DownloadController {

    @Autowired
    private DownloadHandler handler;

    /**
     * 下载入口
     *
     * @param videoUrl 视频url
     * @param response 响应流
     * @return void
     * @author Mr.kusch
     * @date 2022/11/24 15:44
     */
    @GetMapping("/common")
    public void getInfo(@RequestParam String videoUrl, HttpServletResponse response) {
        handler.download(PlatformConstants.getRealPlatform(videoUrl), videoUrl, response);
    }

}
