package com.kusch;

import com.kusch.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动注册白嫖某网站的免费节点
 *
 * @author Mr.kusch
 * @date 2023年02月11日 12:25
 */
@SpringBootTest
@Slf4j
public class GetFreeLink {

    @Autowired
    private RestTemplate restTemplate;

    /*
        分为两步：
        1. 找个临时邮箱地址站，摸清楚其获取免费邮箱和接收邮件的接口。
        2. https://acyun.cf/#/register 对这个网站的注册，领取接口进行分析，使用上述邮箱进行注册。
     */

    @Test
    public void test() {
        //获取邮箱
        Map<String, String> tempEmail = getTempEmail();
        String emailPrefix = tempEmail.get("emailPrefix");
        String email = emailPrefix + "@027168.com";
        String ensid = tempEmail.get("sidNew");
        //发送验证码
        sendMail(email);
        //获取mid
        String mid = null;
        for (int i = 0; i < 20; i++) {
            log.info("请求。。。。。。每隔5秒重试，重试共20次！");
            mid = getMid(emailPrefix, ensid);
            if (mid != null)
                break;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Assert.isTrue(StringUtils.isNotBlank(mid), "获取验证码失败！请稍后重试！");
        String verify = getTempEmailVerificationCode(emailPrefix, ensid, mid);
        //注册
        String token = register(email, verify);
        //获取订单ID
        String orderId = pickUpGift(token);
        //下单
        payFree(token, orderId);
        //获取免费节点
        String subscribedLink = getSubscribedLink(token);
    }


    /**
     * 获取临时邮箱,并获取获取验证码的ID，获取的邮箱默认后缀是：  xxxxxxx@027168.com
     */
    public Map<String, String> getTempEmail() {
        // Post   http://24mail.chacuo.net/
        // headers    cookie必须的，最后一位did需要根据获取的邮箱不同 及时修改，否则无法查询收到的邮件
        // 表单参数： data=&type=renew&arg=d=027168.com_f=
        String url = "http://24mail.chacuo.net/";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("data", "123");
        params.set("type", "renew");
        params.set("arg", "d=027168.com_f=");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Origin", "http://24mail.chacuo.net");
        headers.set("Referer", "http://24mail.chacuo.net");
        headers.set("X-Requested-With", "XMLHttpRequest");
        //必不可少的参数！！！
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/109.0.0.0 Safari/537.36");
        headers.set("Cookie",
                "__yjs_duid=1_f1d5ceb4848cfb554b17f446eb0f3b461676020476954; " +
                        "Hm_lvt_ef483ae9c0f4f800aefdf407e35a21b3=1676020480; " +
                        "yjs_js_security_passport=3e0adf24e57c5ed31adf85959b4f09b8e0f2ccd6_1676025949_js; " +
                        "mail_ck=21; Hm_lpvt_ef483ae9c0f4f800aefdf407e35a21b3=1676089109; " +
                        "sid=4e0cff9d846f8de05e5706979a23afb6d2ca54fb");
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,
                headers), String.class);
        String body = exchange.getBody();
        if (StringUtils.isBlank(body)) {
            throw new RuntimeException("获取临时邮箱失败！");
        }
        Map<String, Object> map = JsonUtils.jsonToMap(body);
        List<String> list = (List<String>) map.get("data");
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("邮箱服务未返回邮箱！");
        }
        Map<String, String> result = new HashMap<>();
        result.put("emailPrefix", list.get(0));
        List<String> sid = exchange.getHeaders().get("Set-Cookie");
        Assert.isTrue(!CollectionUtils.isEmpty(sid), "新的sid不存在！！！！");
        result.put("sidNew", sid.get(0));
        return result;
    }

    /**
     * 获取临时邮箱的mid，等下获取邮件详细信息的时候要用！
     */
    public String getMid(String emailPrefix, String newSid) {

        String url = "http://24mail.chacuo.net/";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("data", emailPrefix);
        params.set("type", "refresh");
        params.set("arg", "");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Origin", "http://24mail.chacuo.net");
        headers.set("Referer", "http://24mail.chacuo.net");
        headers.set("X-Requested-With", "XMLHttpRequest");
        //必不可少的参数！！！
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/109.0.0.0 Safari/537.36");
        headers.set("Cookie",
                "__yjs_duid=1_f1d5ceb4848cfb554b17f446eb0f3b461676020476954; " +
                        "Hm_lvt_ef483ae9c0f4f800aefdf407e35a21b3=1676020480; " +
                        "yjs_js_security_passport=3e0adf24e57c5ed31adf85959b4f09b8e0f2ccd6_1676025949_js; " +
                        "mail_ck=21; Hm_lpvt_ef483ae9c0f4f800aefdf407e35a21b3=1676089109; " +
                        newSid);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,
                headers), String.class);
        Assert.isTrue(exchange.getBody() != null, "获取收到的邮件列表失败！");
        //获取MID，循环获取收到的邮件时有用
        Map<String, Object> map = JsonUtils.jsonToMap(exchange.getBody());
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("data");
        List<Map<String, Object>> real = (List<Map<String, Object>>) list.get(0).get("list");
        if (CollectionUtils.isEmpty(real)) {
            return null;
        }
        Map<String, Object> dd = real.get(0);
        log.info("获取Mid成功！");
        return dd.get("MID").toString();
    }

    public String getTempEmailVerificationCode(String emailPrefix, String newSid, String mid) {
        String url = "http://24mail.chacuo.net/";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("data", emailPrefix);
        params.set("type", "mailinfo");
        params.set("arg", "f=" + mid);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Origin", "http://24mail.chacuo.net");
        headers.set("Referer", "http://24mail.chacuo.net");
        headers.set("X-Requested-With", "XMLHttpRequest");
        //必不可少的参数！！！
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/109.0.0.0 Safari/537.36");
        headers.set("Cookie",
                "__yjs_duid=1_f1d5ceb4848cfb554b17f446eb0f3b461676020476954; " +
                        "Hm_lvt_ef483ae9c0f4f800aefdf407e35a21b3=1676020480; " +
                        "yjs_js_security_passport=3e0adf24e57c5ed31adf85959b4f09b8e0f2ccd6_1676025949_js; " +
                        "mail_ck=21; Hm_lpvt_ef483ae9c0f4f800aefdf407e35a21b3=1676089109; " +
                        newSid);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,
                headers), String.class);
        Assert.isTrue(exchange.getBody() != null, "获取收到的邮件详细信息失败！");
        Map<String, Object> map = JsonUtils.jsonToMap(exchange.getBody());
        List<Map<String, Object>> data = (List<Map<String, Object>>) map.get("data");
        List<Map<String, Object>> emailInfo = (List<Map<String, Object>>) data.get(0);
        List<Map<String, Object>> emailInfoSon = (List<Map<String, Object>>) emailInfo.get(1);
        Map<String, Object> emailMap = emailInfoSon.get(0);
        List<String> htmlList = (List<String>) emailMap.get("DATA");
        String html = htmlList.get(0);
        Document parse = Jsoup.parse(html);
        String style = parse.getElementsByAttributeValue("style", "font-family: 'Helvetica Neue',Helvetica,Arial," +
                "sans-serif; box-sizing: border-box; font-size: 36px; font-weight: bold; text-align: center; color: " +
                "#4a4a4a; vertical-align: top; line-height: 1.6em; margin: 0; padding: 0 0 20px;").text();
        if (StringUtils.isBlank(style)) {
            log.info("从邮件中获取验证码失败！");
        }
        log.info("从邮件中获取验证码成功：{}", style);
        return style;
    }


    /**
     * 发送验证码
     */
    public void sendMail(String email) {
        //Post https://acyun.cf/api/v1/passport/comm/sendEmailVerify
        //FormData email=msctzx65471%40027168.com
        String url = "https://acyun.cf/api/v1/passport/comm/sendEmailVerify";
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", "application/x-www-form-urlencoded");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("email", email);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,
                headers), String.class);
        if (null != exchange.getBody() && exchange.getBody().contains("true")) {
            log.info("发送邮件给：{}，成功！", email);
        } else {
            throw new RuntimeException("发送邮件给：" + email + "，失败！");
        }
    }

    /**
     * 注册账号
     */
    public String register(String email, String code) {
        String password = "Password123";
        String url = "https://acyun.cf/api/v1/passport/auth/register";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("email", email);
        params.set("password", password);
        params.set("invite_code", "");
        params.set("email_code", code);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Origin", "https://acyun.cf");
        headers.set("Referer", "https://acyun.cf");
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/109.0.0.0 Safari/537.36");
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,
                headers), String.class);
        String body = exchange.getBody();
        Assert.isTrue(null != body, "注册失败！");
        Map<String, Object> map = JsonUtils.jsonToMap(body);
        Map<String, String> data = (Map<String, String>) map.get("data");
        String token = data.get("auth_data");
        log.info("账号：{} ,密码：{} 注册成功！token: {}", email, password, token);
        return token;
    }

    /**
     * 获取订单ID
     */
    public String pickUpGift(String token) {
        String url = "https://acyun.cf/api/v1/user/order/save";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("period", "month_price");
        params.set("plan_id", "4");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Origin", "https://acyun.cf");
        headers.set("Referer", "https://acyun.cf");
        headers.set("authorization", token);
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/109.0.0.0 Safari/537.36");
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,
                headers), String.class);
        String body = exchange.getBody();
        Assert.isTrue(null != body, "获取订单ID失败！");
        Map<String, Object> map = JsonUtils.jsonToMap(body);
        String data = (String) map.get("data");
        Assert.isTrue(StringUtils.isNotBlank(data), "解析订单ID失败！");
        log.info("获取订单id成功：{}", data);
        return data;
    }

    /**
     * 下单免费节点
     */
    public void payFree(String token, String orderId) {
        String url = "https://acyun.cf/api/v1/user/order/checkout";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("trade_no", orderId);
        params.set("method", "1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Origin", "https://acyun.cf");
        headers.set("Referer", "https://acyun.cf");
        headers.set("authorization", token);
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/109.0.0.0 Safari/537.36");
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(params,
                headers), String.class);
        String body = exchange.getBody();
        assert body != null;
        if (body.contains("true")) {
            log.info("免费套餐下单成功！");
            try {
                //睡眠1s保证下单完成！
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("下单失败！body: " + body);
        }
    }

    /**
     * 获取订阅链接
     */
    public String getSubscribedLink(String token) {
        String url = "https://acyun.cf/api/v1/user/getSubscribe";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Origin", "https://acyun.cf");
        headers.set("Referer", "https://acyun.cf");
        headers.set("authorization", token);
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like " +
                "Gecko) Chrome/109.0.0.0 Safari/537.36");
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(params,
                headers), String.class);
        String body = exchange.getBody();
        assert body != null;
        Map<String, Object> map = JsonUtils.jsonToMap(body);
        Map<String, String> data = (Map<String, String>) map.get("data");
        String subscribeUrl = data.get("subscribe_url");
        if (StringUtils.isBlank(subscribeUrl)) {
            throw new RuntimeException("获取订阅链接失败！");
        }
        log.info("订阅链接：{}", subscribeUrl);
        return subscribeUrl;
    }
}