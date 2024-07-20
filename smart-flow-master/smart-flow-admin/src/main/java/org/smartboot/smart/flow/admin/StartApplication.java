package org.smartboot.smart.flow.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author qinluo
 * @date 2023-01-29 11:59:47
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"org.smartboot.smart.flow.admin"})
@MapperScan("org.smartboot.smart.flow.admin.mapper")
@EnableWebMvc
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}
