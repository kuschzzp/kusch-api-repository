package com.kusch.service.impl;

import com.kusch.service.ClashNodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 获取指定类型的node节点
 *
 * @author Mr.Kusch
 * @date 2023年01月15日 16:13
 */
@Service
@Slf4j
public class ClashNodeServiceImpl implements ClashNodeService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void getTaregtClashNodeService(String type, HttpServletRequest request,
                                          HttpServletResponse response) {
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

        if (!"txt".equals(type) && !"yaml".equals(type)) {
            try {
                response.getWriter().write("仅支持txt和yaml两个值！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String result = null;
        for (String pText : pTexts) {
            if (StringUtils.contains(pText, "https://clashnode.com/wp-content")
                    && StringUtils.contains(pText, type)) {
                result = pText;
                break;
            }
        }

        try {
            doResponse(result, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doResponse(String realUrl, HttpServletResponse response) throws IOException {
        File historyFile = new File("./history.txt");
        if (!historyFile.exists()) {
            boolean create = historyFile.createNewFile();
            log.info("初始化创建history文件---->  {}" + (create ? "success" : "fail"));
        }
        String doResponseUrl;
        if (StringUtils.isBlank(realUrl)) {
            //本次没有读到对应的URL，返回上一次的URL内容
            doResponseUrl = FileUtils.readFileToString(historyFile, StandardCharsets.UTF_8);
            if (StringUtils.isBlank(doResponseUrl)) {
                log.error("初始化文件为空，并且读取到的为空！");
                return;
            }
        } else {
            //先将链接更新到文件里去
            FileUtils.write(historyFile, realUrl, StandardCharsets.UTF_8, false);
            doResponseUrl = realUrl;
        }
        //下载文件，流返回
        ResponseEntity<byte[]> responseEntity
                = restTemplate.getForEntity(doResponseUrl, byte[].class);
        byte[] body = responseEntity.getBody();
        Assert.isTrue(null != body, "配置文件响应为空！");

        response.setHeader("Access-Control-Expose-Headers", "*");
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + "freeNodeEveryDay.yaml");
        IOUtils.copy(new ByteArrayInputStream(body), response.getOutputStream());
    }
}
