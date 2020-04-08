package com.heartsuit.logback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class LogbackApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogbackApplication.class, args);
        log.trace("logback trace");
        log.debug("logback debug");
        log.info("logback info");
        log.warn("logback warn");
        log.error("logback error");
    }

}
