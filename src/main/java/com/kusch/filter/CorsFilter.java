package com.kusch.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域过滤器
 *
 * @author Mr.kusch
 * @date 2023/5/24 11:43
 */
@Component
@Order(-200)
public class CorsFilter implements Filter {

    static final String OPTIONS = "OPTIONS";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 允许指定域访问跨域资源
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 允许所有请求方式
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        // 有效时间
        response.setHeader("Access-Control-Max-Age", "3600");
        // 允许的header参数
        response.setHeader("Access-Control-Allow-Headers", "*");
        // 允许cookies跨域
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 如果是预检请求，直接返回
        if (OPTIONS.equals(request.getMethod())) {
            response.getOutputStream().write("".getBytes());
            return;
        }
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
