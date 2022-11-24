package com.kusch.controller;

import com.kusch.service.ParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 对外接口
 *
 * @author Mr.kusch
 * @date 2022年11月21日 15:50
 */
@RestController
@RequestMapping("/download")
@CrossOrigin
public class DownController {

    @Autowired
    private ParseService parseService;

    @GetMapping("/common")
    public void getInfo(@RequestParam String videoUrl, HttpServletResponse response) {
        response.setHeader("Access-Control-Expose-Headers", "*");
        response.setContentType("application/octet-stream;charset=utf-8");
        parseService.parse(videoUrl, response);
    }

}
