package com.kusch.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestResponse;
import com.kusch.service.HhmSignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 签到的实现类
 *
 * @author Mr.Kusch
 * @date 2023年05月05日 15:44
 */
@Service
@Slf4j
public class HhmSignServiceImpl implements HhmSignService {

    private static final Map<String, String> INIT_MAP = new HashMap<>();

    static {
        INIT_MAP.put("1526938209@qq.com", "qwer1234");
        INIT_MAP.put("qqqqwwww", "qqqqwwww");
        INIT_MAP.put("qqqqwwwweeee", "qqqqwwwweeee");
        INIT_MAP.put("zzzzxxxx", "zzzzxxxx");
        INIT_MAP.put("ccccvvvv", "ccccvvvv");
    }

    @Override
    public void hengHengMaoSign() throws InterruptedException {
        for (Map.Entry<String, String> entry : INIT_MAP.entrySet()) {
            qiandao(entry.getKey(), entry.getValue());
            Thread.sleep(6000);
        }
    }

    private void qiandao(String user, String pass) {
        ForestResponse response =
                Forest.post("https://video.baoge.vip/user/login.php")
                        .addBody("user=" + user + "&pass=" + pass)
                        .execute(ForestResponse.class);
        log.info(user + "登陆：  " + response.getContent());
        String cookie =
                response.getHeaders().getHeaders("Set-Cookie")
                        .stream().map(ForestHeader::getValue)
                        .collect(Collectors.joining(";"));

        ForestResponse qiandao = Forest.get("https://video.baoge.vip/user/qiandao.php")
                .addHeader("Cookie", cookie)
                .execute(ForestResponse.class);
        //去掉所有的空格制表等空字符
        String str = qiandao.getContent().replaceAll("\\s", "");
        // 获取 varu= 的位置
        int index = str.indexOf("varu=");
        String u = "";
        // 如果找到了 varu=
        if (index != -1) {
            // 获取 varu= 后面的子字符串
            String numStr = str.substring(index + 5);
            StringBuilder sb = new StringBuilder();
            // 遍历每个字符
            for (char c : numStr.toCharArray()) {
                // 如果是数字
                if (Character.isDigit(c)) {
                    // 添加到结果中
                    sb.append(c);
                } else {
                    // 如果不是数字，则结束循环
                    break;
                }
            }
            if (sb.length() > 0) {
                u = sb.toString();
            }
        }
        long t = System.currentTimeMillis();
        String s = MD5.create().digestHex(u + "qd396qsy1110" + t);
        String url = "https://video.baoge.vip/user/ajax.php?act=qiandao&t=" + t + "&s=" + s;
        ForestResponse qandao = Forest.get(url).addHeader("Cookie",
                        cookie)
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/113.0.0.0 Safari/537.36")
                .execute(ForestResponse.class);
        log.info(user + "签到" + qandao.getContent());

    }
}
