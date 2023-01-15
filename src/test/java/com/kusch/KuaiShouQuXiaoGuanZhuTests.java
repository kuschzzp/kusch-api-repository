package com.kusch;

import com.kusch.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootTest
class KuaiShouQuXiaoGuanZhuTests {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void KuaiShouQuxiaoGuanzhu() throws InterruptedException {
        Set<String> set = new HashSet<>();
        for (int i = 2; i < 200; i++) {
            set.addAll(getIds(i));
        }

        for (String id : set) {
            unFollow(id);
        }

    }


    private List<String> getIds(int start) throws InterruptedException {
        Thread.sleep(500);

        List<String> list = new ArrayList<>();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", "did=web_666cd622737a41f99c84ce28c94943ff; didv=1669455498000; kpf=PC_WEB; " +
                "kpn=KUAISHOU_VISION; clientid=3; userId=113879621; kuaishou.server" +
                ".web_st=ChZrdWFpc2hvdS5zZXJ2ZXIud2ViLnN0EqAB2pbmIX4Mk2qyMZACYtg0qC_jxzox_" +
                "-vDlAj6k4KwVBUhqb07vpmIkE6WvkzFgIroRZLD4aMRfdTHxQjkXfYfTqXtuUPn2JoifKqF_WkYRcOVCdxPDVFsrhn68DyhnsQNoZX4nHY7-YqBr6cRG6B5dXdTnuAd-ewdSbMNIO0miwlb9NX8bu1ocu2Kgdjx6CfsKIkZFq4Zj8iN_msE24M0YhoSzFZBnBL4suA5hQVn0dPKLsMxIiC7lRcgpsKV8L323che4u1A1OxG8YHDKWq1siNiyGF_qSgFMAE; kuaishou.server.web_ph=86f029d4ba7306aaff6067c01e141dca9470");
        httpHeaders.add("Referer", "https://www.kuaishou.com/profile/3x4qpb4nvuqpw32");
        httpHeaders.add("Origin", "https://www.kuaishou.com");
        MediaType type = MediaType.parseMediaType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpHeaders.setContentType(type);
        Map<String, Object> map = JsonUtils.jsonToMap("{\n" +
                "    \"operationName\": \"visionProfileUserList\",\n" +
                "    \"variables\": {\n" +
                "        \"ftype\": 1,\n" +
                "        \"pcursor\": \"" + start + "\"\n" +
                "    },\n" +
                "    \"query\": \"query visionProfileUserList($pcursor: String, $ftype: Int) {\\n  " +
                "visionProfileUserList(pcursor: $pcursor, ftype: $ftype) {\\n    result\\n    fols {\\n      " +
                "user_name\\n      headurl\\n      user_text\\n      isFollowing\\n      user_id\\n      " +
                "__typename\\n    }\\n    hostName\\n    pcursor\\n    __typename\\n  }\\n}\\n\"\n" +
                "}");

        ResponseEntity<String> exchange = restTemplate.exchange("https://www.kuaishou.com/graphql", HttpMethod.POST,
                new HttpEntity<>(map, httpHeaders),
                String.class);

        Map<String, Object> body = JsonUtils.jsonToMap(exchange.getBody());
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        Map<String, Object> visionProfileUserList = (Map<String, Object>) data.get("visionProfileUserList");
        List<Map<String, Object>> fols = (List<Map<String, Object>>) visionProfileUserList.get("fols");
        for (Map<String, Object> fol : fols) {
            Object user_id = fol.get("user_id");
            list.add((String) user_id);
        }
        return list;
    }

    private void unFollow(String id) throws InterruptedException {

        Thread.sleep(500);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Cookie", "did=web_666cd622737a41f99c84ce28c94943ff; didv=1669455498000; kpf=PC_WEB; " +
                "kpn=KUAISHOU_VISION; clientid=3; userId=113879621; kuaishou.server" +
                ".web_st=ChZrdWFpc2hvdS5zZXJ2ZXIud2ViLnN0EqAB2pbmIX4Mk2qyMZACYtg0qC_jxzox_" +
                "-vDlAj6k4KwVBUhqb07vpmIkE6WvkzFgIroRZLD4aMRfdTHxQjkXfYfTqXtuUPn2JoifKqF_WkYRcOVCdxPDVFsrhn68DyhnsQNoZX4nHY7-YqBr6cRG6B5dXdTnuAd-ewdSbMNIO0miwlb9NX8bu1ocu2Kgdjx6CfsKIkZFq4Zj8iN_msE24M0YhoSzFZBnBL4suA5hQVn0dPKLsMxIiC7lRcgpsKV8L323che4u1A1OxG8YHDKWq1siNiyGF_qSgFMAE; kuaishou.server.web_ph=86f029d4ba7306aaff6067c01e141dca9470");
        httpHeaders.add("Referer", "https://www.kuaishou.com/profile/3x4qpb4nvuqpw32");
        httpHeaders.add("Origin", "https://www.kuaishou.com");
        MediaType type = MediaType.parseMediaType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpHeaders.setContentType(type);
        Map<String, Object> map = JsonUtils.jsonToMap("{\n" +
                "    \"operationName\": \"visionFollow\",\n" +
                "    \"variables\": {\n" +
                "        \"touid\": \"" + id + "\",\n" +
                "        \"ftype\": 2,\n" +
                "        \"followSource\": 1\n" +
                "    },\n" +
                "    \"query\": \"mutation visionFollow($touid: String, $ftype: Int, $followSource: Int, $expTag: " +
                "String) {\\n  visionFollow(touid: $touid, ftype: $ftype, followSource: $followSource, expTag: " +
                "$expTag) {\\n    result\\n    followStatus\\n    hostName\\n    error_msg\\n    __typename\\n  " +
                "}\\n}\\n\"\n" +
                "}");

        ResponseEntity<String> exchange = restTemplate.exchange("https://www.kuaishou.com/graphql", HttpMethod.POST,
                new HttpEntity<>(map, httpHeaders),
                String.class);

        System.out.println(exchange.getBody());
    }


}
