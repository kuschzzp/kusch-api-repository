package com.kusch.apis.net.controller;

import cn.hutool.json.JSONObject;
import com.kusch.apis.net.service.CodeOnlineNetworkApiService;
import com.kusch.apis.net.service.DeclareOnlineNetworkApiService;
import com.kusch.result.R;
import com.kusch.utils.RegxUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 网络公开的API接口整理
 *
 * @author Mr.kusch
 * @date 2023/5/26 14:33
 */
@RestController
@RequestMapping("/a")
public class OnlineNetworkApiController {

    @Resource
    private DeclareOnlineNetworkApiService declareService;

    @Resource
    private CodeOnlineNetworkApiService codeService;

    /**
     * 每日一言
     *
     * @return R
     */
    @GetMapping("/word")
    public R<String> word() {
        return R.ok(declareService.dailyWord());
    }

    /**
     * 历史上的今天
     *
     * @return R
     */
    @GetMapping("/history")
    public R<Object> historyDay() {
        Object resData = declareService.historyDay();
        if (ObjectUtils.isNotEmpty(resData)) {
            return R.ok(new JSONObject(resData).getJSONObject("data"));
        }
        return R.fail();
    }

    /**
     * 视频链接提取
     * <p>
     * 支持列表: 皮皮虾, 抖音, 微视, 快手, 6间房, 哔哩哔哩, 微博, 绿洲, 度小视, 开眼, 陌陌, 皮皮搞笑, 全民k歌,逗拍, 虎牙, 新片场, 哔哩哔哩, Acfun, 美拍, 西瓜视频, 火山小视频,
     * 网易云Mlog, 好看视频。 《《《温馨提示: 哔哩哔哩/6间房/微博仅支持下载无法去除水印》》》
     * <p/>
     *
     * @param url 视频的链接
     * @return R
     */
    @GetMapping("/video")
    public R<Object> video(String url) {
        Assert.isTrue(StringUtils.isNotBlank(url), "请提供视频分享链接！");
        String realUrl = RegxUtils.getRealUrl(url);
        Object resData = declareService.videoDownload(realUrl);
        if (ObjectUtils.isNotEmpty(resData)) {
            return R.ok(new JSONObject(resData).getJSONObject("data"));
        }
        return R.fail();
    }

    /**
     * 图集链接提取
     * <p>
     * 支持列表: 支持列表: 皮皮虾, 抖音, 快手, 皮皮搞笑, 最右, 微博
     * <p/>
     *
     * @param url 图集的链接
     * @return R
     */
    @GetMapping("/images")
    public R<Object> images(String url) {
        Assert.isTrue(StringUtils.isNotBlank(url), "请提供图集分享链接！");
        String realUrl = RegxUtils.getRealUrl(url);
        Object resData = declareService.imagesDownload(realUrl);
        if (ObjectUtils.isNotEmpty(resData)) {
            return R.ok(new JSONObject(resData).getJSONObject("data"));
        }
        return R.fail();
    }


}
