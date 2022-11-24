package com.kusch.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 自定义请求拦截器做点事儿
 *
 * @author Mr.kusch
 * @date 2022/11/23 13:45
 */
@Slf4j
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        //打印请求明细
        logRequestDetails(request, body);
        ClientHttpResponse response = execution.execute(request, body);

        // 包装代理一下 让只能读一次的响应流可以继续向下传递，，，不打印响应体了，就先不包装了
//        response = new ClientHttpResponseWrapper(response);

        //打印响应明细
        logResponseDetails(response);

        return response;
    }

    private void logRequestDetails(HttpRequest request, byte[] body) {
        long millis = System.currentTimeMillis();
        log.info("==========================" + millis + "==========================");
        log.info("{}：{}", request.getMethod(), request.getURI());
        log.info("Headers: {}", request.getHeaders());
        log.info("body: {}", new String(body, StandardCharsets.UTF_8));
        log.info("==========================" + millis + "==========================");
    }

    private void logResponseDetails(ClientHttpResponse response) throws IOException {
        long millis = System.currentTimeMillis();
        log.info("==========================" + millis + "==========================");
        log.info("Status code  : {}", response.getStatusCode());
        log.info("Status text  : {}", response.getStatusText());
        log.info("Headers      : {}", response.getHeaders());
        //注意！ 响应流只能读一次，这边打日志读了，后面你获取body不久瓜皮了，获取个der哟，
        // 解决办法是把响应流代理一下 ClientHttpResponseWrapper，，，，一般也不需要全打印，这里就不开打印了
        //        log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        log.info("==========================" + millis + "==========================");
    }

    /**
     * 包装 ClientHttpResponse 以便于 response 可重读
     */
    private static class ClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse clientHttpResponse;
        private byte[] body;

        public ClientHttpResponseWrapper(ClientHttpResponse response) {
            this.clientHttpResponse = response;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return this.clientHttpResponse.getStatusCode();
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return this.clientHttpResponse.getRawStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return this.clientHttpResponse.getStatusText();
        }

        @Override
        public void close() {
            this.clientHttpResponse.close();
        }

        @Override
        public InputStream getBody() throws IOException {
            //缓存body每次返回一个新的输入流
            if (Objects.isNull(this.body)) {
                this.body = StreamUtils.copyToByteArray(this.clientHttpResponse.getBody());
            }
            return new ByteArrayInputStream(this.body == null ? new byte[0] : this.body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.clientHttpResponse.getHeaders();
        }
    }
}