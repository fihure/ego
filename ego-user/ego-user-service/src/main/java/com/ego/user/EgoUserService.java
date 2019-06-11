package com.ego.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.ego.user.mapper")
public class EgoUserService {
    public static void main(String[] args) {
        SpringApplication.run(EgoUserService.class, args);
    }
}
