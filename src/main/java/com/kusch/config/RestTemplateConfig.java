package com.kusch.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .customizers(restTemplateCustomizer())
                .additionalInterceptors(new CustomClientHttpRequestInterceptor())
                //                .errorHandler(xxxxx) //定义错误处理器
                .defaultHeader("user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0" +
                                ".4147.125 Safari/537.36")
                .build();
    }

    /**
     * 定制化RestTemplate
     */
    @Bean
    public RestTemplateCustomizer restTemplateCustomizer() {
        return restTemplate -> {
            //            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            //
            //            //创建连接管理器 默认就支持https http
            //            PoolingHttpClientConnectionManager pM = new PoolingHttpClientConnectionManager();
            //            //最大连接数
            //            pM.setMaxTotal(100);
            //            //同路由并发数
            //            pM.setDefaultMaxPerRoute(20);
            //
            //            httpClientBuilder.setConnectionManager(pM);
            //
            //            //创建httpClient
            //            HttpClient httpClient = httpClientBuilder.build();

            //创建 HttpComponentsClientHttpRequestFactory
            HttpComponentsClientHttpRequestFactory requestFactory =
                    null;
            try {
                requestFactory =
                        new HttpComponentsClientHttpRequestFactory(HttpClientUtils.acceptsUntrustedCertsHttpClient());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //连接超时
            requestFactory.setConnectTimeout(10 * 1000);
            //数据读取超时时间
            requestFactory.setReadTimeout(60 * 1000);
            //连接不够用的等待时间
            requestFactory.setConnectionRequestTimeout(30 * 1000);


            //设置请求工厂
            restTemplate.setRequestFactory(requestFactory);

            //用UTF-8 StringHttpMessageConverter 替换默认 StringHttpMessageConverter
            //可以解决请求回来的数据乱码问题
            List<HttpMessageConverter<?>> newMessageConverters = new ArrayList<>();
            for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
                if (converter instanceof StringHttpMessageConverter) {
                    StringHttpMessageConverter messageConverter
                            = new StringHttpMessageConverter(StandardCharsets.UTF_8);
                    newMessageConverters.add(messageConverter);
                } else {
                    newMessageConverters.add(converter);
                }
            }
            restTemplate.setMessageConverters(newMessageConverters);
        };
    }
}
