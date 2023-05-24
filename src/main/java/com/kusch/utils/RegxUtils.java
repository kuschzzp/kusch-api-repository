package com.kusch.utils;

import com.kusch.ex.ApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author Mr.Kusch
 * @date 2023年05月24日 14:27
 */
public class RegxUtils {

    /**
     * URL提取的正则
     */
    private static final String URL_REGX = "(?i)\\b((?:https?://|www\\d*\\.|m\\.)(?:[^\n\\s()<>]+|\\(([^\\n\\s()<>]+|" +
            "(?:\\([^\\n\\s()<>]+\\)))\\))+(?:\\(([^\\n\\s()<>]+|(?:\\(?:[^\\n\\s()<>]+\\)))\\)|[^\\n\\s`!()" +
            "\\[\\]{};:'\".,<>?«»“”‘’]))";

    public static String getRealUrl(String shareLink) {
        // URL匹配的正则
        Pattern pattern = Pattern.compile(URL_REGX);

        List<String> list = new ArrayList<>();

        Matcher matcher = pattern.matcher(shareLink);
        while (matcher.find()) {
            list.add(matcher.group(0));
        }
        if (list.size() != 1) {
            throw new ApiException("请手动删除非链接内容后重试！");
        }
        return list.get(0);
    }

}
