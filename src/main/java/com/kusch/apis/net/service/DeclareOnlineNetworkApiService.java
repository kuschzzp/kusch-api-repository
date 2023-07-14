package com.kusch.apis.net.service;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;

/**
 * 声明式网络接口调用service接口
 *
 * @author Mr.kusch
 * @date 2023/5/26 17:36
 */
public interface DeclareOnlineNetworkApiService {

    /**
     * 每日一言
     *
     * @return java.lang.String
     */
    @Get(url = "https://tenapi.cn/v2/yiyan?format=string")
    String dailyWord();

    /**
     * 历史上的今天
     *
     * @return java.lang.String
     */
    @Get(url = "https://tenapi.cn/v2/history")
    Object historyDay();

    /**
     * 下载视频
     *
     * @param url 视频分享链接
     * @return object
     */
    @Post(url = "https://tenapi.cn/v2/video", headers = {
            "Content-Type: application/x-www-form-urlencoded",
    })
    Object videoDownload(@Body("url") String url);

    /**
     * 下载图集
     *
     * @param url 图集分享链接
     * @return object
     */
    @Post(url = "https://tenapi.cn/v2/images", headers = {
            "Content-Type: application/x-www-form-urlencoded",
    })
    Object imagesDownload(@Body("url") String url);

}
