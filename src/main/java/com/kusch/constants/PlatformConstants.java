package com.kusch.constants;

import com.kusch.ex.ApiException;

/**
 * 支持平台的常量
 *
 * @author Mr.Kusch
 * @date 2022年11月24日 15:26
 */
public class PlatformConstants {
    /**
     * 抖音
     */
    public final static String DOU_YIN = "douyin";

    /**
     * 皮皮虾
     */
    public final static String PI_PI_XIA = "pipix";

    /**
     * 最右
     */
    public static final String ZUI_YOU = "xiaochuankeji";

    /**
     * 快手
     */
    public static final String KUAI_SHOU = "kuaishou";

    /**
     * 哔哩哔哩站标识
     */
    public static final String BILIBILI = "bbbbbbbbbbbb";

    /**
     * 哔哩哔哩网页端分享链接
     */
    public static final String BILIBILI_WEB = "bilibili";

    /**
     * 哔哩哔哩手机端分享链接
     */
    public static final String BILIBILI_PHONE = "b23.tv";


    public static String getRealPlatform(String url) {
        if (url.contains(DOU_YIN)) {
            return DOU_YIN;
        } else if (url.contains(PI_PI_XIA)) {
            return PI_PI_XIA;
        } else if (url.contains(ZUI_YOU)) {
            return ZUI_YOU;
        } else if (url.contains(KUAI_SHOU)) {
            return KUAI_SHOU;
        } else if (url.contains(BILIBILI_PHONE) || url.contains(BILIBILI_WEB)) {
            return BILIBILI;
        } else {
            throw new ApiException(url + "----暂不支持该平台");
        }
    }

    //******************************************其他常量******************************************

    /**
     * 把链接丢响应头里面去
     */
    public static final String REAL_URL = "real_url";

    /**
     * 流返回
     */
    public static final String DOWNLOAD_STREAM = "0";

    /**
     * URL返回
     */
    public static final String DOWNLOAD_URL = "1";

}
