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
     * 例如：127.0.0.1:17777/download/common?videoUrl=https://v.kuaishou.com/CHwRjS&way=0
     *
     * @param videoUrl 视频url
     * @param way      下载方式   0 流下载  1 直接返回URL
     * @param response 响应流
     * @return void
     * @author Mr.kusch
     * @date 2022/11/24 15:44
     */
    @GetMapping("/common")
    public void getInfo(@RequestParam String videoUrl,
                        @RequestParam String way,
                        HttpServletResponse response) {
        handler.download(PlatformConstants.getRealPlatform(videoUrl), videoUrl, way, response);
    }

}
