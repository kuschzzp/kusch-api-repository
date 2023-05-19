package com.kusch;

import com.kusch.service.HhmSignService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
class HengHengMaoTests {

    @Autowired
    private HhmSignService service;

    @Test
    public void qianDao() throws Exception {

        service.hengHengMaoSign();

    }
}
