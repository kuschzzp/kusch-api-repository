package com.kusch.service;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 通用解析Service
 *
 * @author Mr.kusch
 * @date 2022/11/21 15:53
 */
public interface DownloadService {

    /**
     * 下载视频方法
     *
     * @param response 响应流
     * @param filename 文件名
     * @param body     获取到的内容
     * @return void
     * @author Mr.kusch
     * @date 2022/11/24 15:05
     */
    default void download(HttpServletResponse response, String filename, byte[] body) throws IOException {
        response.setHeader("Access-Control-Expose-Headers", "*");
        response.setContentType("application/octet-stream;charset=utf-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        // 此处的编码是文件名的编码，使能正确显示中文文件名
        try {
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + filename
                            + ";filename*=utf-8''" +
                            URLEncoder.encode(filename, "utf-8") + ".mp4");
            response.addHeader("realname", URLEncoder.encode(filename, "utf-8") + ".mp4");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        IOUtils.copy(byteArrayInputStream, response.getOutputStream());
    }

    /**
     * 解析并获取响应内容
     *
     * @param url      分享链接
     * @param response 响应流
     * @return void
     * @author Mr.kusch
     * @date 2022/11/24 15:06
     */
    void parse(String url, HttpServletResponse response);

    /**
     * 平台，维护在常量类 PlatformConstants.java
     *
     * @return java.lang.String
     * @author Mr.kusch
     * @date 2022/11/26 16:35
     */
    String platform();
}
