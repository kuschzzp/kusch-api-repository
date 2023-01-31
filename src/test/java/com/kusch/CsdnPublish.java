package com.kusch;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * CSDN文章发布调试
 *
 * @author Zhangzp
 * @date 2023年01月17日 16:28
 */
@SpringBootTest
public class CsdnPublish {

    @Test
    public void tttt() throws Exception {
        //固定的字符串
        // x-ca-key : 203803574
        // appSecret : 9znpamsyl2c7cdrr9sas0le9vbc3r6ba
        System.out.println("x-ca-key:203803574");
        String xCaNonce = getXCaNonce();
        System.out.println("x-ca-nonce:" + xCaNonce);
//                System.out.println("x-ca-signature:"
//                        + getXCaSignature(
//                        getSignaturePartString(
//                                "get",
//                                xCaNonce,
//                                "/blog/phoenix/console/v1/article/list?pageSize=20&status=deleted"),
//                        "9znpamsyl2c7cdrr9sas0le9vbc3r6ba"));
        System.out.println("x-ca-signature:"
                + getXCaSignature(
                getSignaturePartString(
                        "post",
                        xCaNonce,
                        "/blog-console-api/v3/mdeditor/saveArticle"),
                "9znpamsyl2c7cdrr9sas0le9vbc3r6ba"));
        System.out.println("x-ca-signature-headers:x-ca-key,x-ca-nonce");
    }

    private String getSignaturePartString(String method, String xCaNonce, String url) {
        if ("get".equals(method)) {
            return "GET\napplication/json, text/plain, */*\n\n\n\nx-ca-key:203803574\nx-ca-nonce:" + xCaNonce + "\n" +
                    url;
        } else {
            return "POST\n*/*\n\napplication/json\n\nx-ca-key:203803574\nx-ca-nonce:" + xCaNonce + "\n" +
                    url;
        }
    }

    private static String getXCaSignature(String content, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secret_key);
        byte[] binaryData = mac.doFinal(content.getBytes());
        System.err.println(new String(binaryData));
        return Base64.encodeBase64String(binaryData);
    }

    public String getXCaNonce() {
        //"xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,
        // (function(e) {var n = 16 * 0.9 | 0, t = "x" === e ? n : 3 & n | 8;return t.toString(16)}))
        char[] chars = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char item : chars) {
            String s = String.valueOf(item);
            if (!"x".equals(s) && !"y".equals(s)) {
                sb.append(s);
                continue;
            }
            sb.append(dealXy(s));
        }
        return sb.toString();
    }


    private String dealXy(String e) {
        int n = new Double(16 * Math.random()).intValue();
        int t;
        if ("x".equals(e)) {
            t = (n | 8);
        } else {
            t = (3 & n | 8);
        }
        return Integer.toHexString(t);
    }


}
