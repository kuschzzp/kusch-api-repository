package com.kusch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author Mr.kusch
 * @date 2022/11/24 14:59
 */
@SpringBootApplication
@EnableScheduling
public class ApiRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiRepositoryApplication.class, args);
    }

}
