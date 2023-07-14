package com.kusch.apis.local.controller;

import com.kusch.apis.local.service.LocalApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 本方法调用产生的api接口
 *
 * @author Mr.kusch
 * @date 2023/5/26 17:33
 */
@RestController
@RequestMapping("/b")
public class LocalApiController {

    @Resource
    LocalApiService localApiService;


}
