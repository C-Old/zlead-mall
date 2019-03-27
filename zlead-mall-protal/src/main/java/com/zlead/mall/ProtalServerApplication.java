package com.zlead.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProtalServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProtalServerApplication.class, args);
    }
}
