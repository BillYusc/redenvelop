package com.scc.redenvelop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedenvelopApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedenvelopApplication.class, args);
    }

}
