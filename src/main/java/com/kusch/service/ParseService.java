package com.kusch.service;

import javax.servlet.http.HttpServletResponse;

/**
 * 通用解析Service
 *
 * @author Mr.kusch
 * @date 2022/11/21 15:53
 */
public interface ParseService {
    /**
     * 解析
     *
     * @param url 通用链接
     * @param response
     * @return java.lang.Object
     * @author Mr.kusch
     * @date 2022/11/21 15:56
     */
    void parse(String url, HttpServletResponse response);
}
