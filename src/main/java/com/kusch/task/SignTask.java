package com.kusch.task;

import com.kusch.service.HhmSignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 签到任务的集合
 *
 * @author Mr.Kusch
 * @date 2023年05月05日 15:54
 */
@Component
@Slf4j
public class SignTask {


    @Autowired
    private HhmSignService service;

    @Scheduled(cron = "0 1 9 * * ?")
    public void hengHengMao() throws InterruptedException {
        service.hengHengMaoSign();
    }
}
