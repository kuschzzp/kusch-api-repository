package com.kusch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class MianFeiJieDianTests {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void clashNodeTest() throws IOException {
        String freeAllPage = restTemplate.getForObject("https://clashnode.com/f/freenode", String.class);
        Document parse = Jsoup.parse(Objects.requireNonNull(freeAllPage));
        Element body = parse.body();
        Elements mainInfos = body.getElementsByAttribute("cp-post-title");
        Element first = mainInfos.first();
        Assert.isTrue(null != first, "没有获取到根结点！！！！");
        String realPage = first.getElementsByAttribute("target").attr("href");
        Assert.isTrue(StringUtils.isNotBlank(realPage), "没有获取到页面URL！！！");
        String toFindUrl = restTemplate.getForObject(realPage, String.class);
        Document toFindUrlPage = Jsoup.parse(Objects.requireNonNull(toFindUrl));
        Elements elementsContent = toFindUrlPage.getElementsByClass("post-content-content");
        Element onlyOneElementsContent = elementsContent.first();
        Assert.isTrue(null != onlyOneElementsContent, "没有发布节点！！！");
        Elements p = onlyOneElementsContent.getElementsByTag("p");
        List<String> pTexts = p.eachText();

        for (String pText : pTexts) {
            if (StringUtils.contains(pText, "https://clashnode.com/wp-content")
                    && StringUtils.contains(pText, ".yaml")) {
                ClassPathResource classPathResource = new ClassPathResource("./history.txt");
                File historyFile = classPathResource.getFile();
                FileUtils.write(historyFile, pText, StandardCharsets.UTF_8, false);
                break;
            }
        }

    }
}
