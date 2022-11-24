package com.kusch.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用一些方法
 *
 * @author Zhangzp
 * @date 2022年11月23日 10:34
 */
public class CommonUtils {

    private static final String UNICODE_PATTERN = "(\\\\u(\\p{XDigit}{4}))";

    /**
     * unicode解码
     */
    public static String unicodeDecode(String string) {
        Pattern pattern = Pattern.compile(UNICODE_PATTERN);
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }

    /**
     * unicode编码
     */
    public static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

}
