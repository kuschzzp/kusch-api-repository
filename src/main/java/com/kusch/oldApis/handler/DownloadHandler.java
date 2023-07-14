package com.kusch.oldApis.handler;

import com.kusch.oldApis.service.DownloadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略模式调度类
 *
 * @author Mr.kusch
 * @date 2022年11月24日 15:21
 */
@Component
public class DownloadHandler implements ApplicationContextAware, InitializingBean {

    /**
     * 具体实现类Map
     */
    private final Map<String, DownloadService> handlerMap = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    @Override
    @SuppressWarnings("all")
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        //将其实现类包装进一个map中
        applicationContext.getBeansOfType(DownloadService.class).forEach((k, v) -> {
            if (StringUtils.isBlank(v.platform())) {
                return;
            }
            handlerMap.put(v.platform(), v);
        });
    }

    /**
     * 根据类型处理下载操作
     *
     * @param type     见 PlatformConstants.java
     * @param url      参数
     * @param response 响应流
     */
    public void download(String type, String url, String way, HttpServletResponse response) {
        handlerMap.get(type).parse(url, way, response);
    }
}
