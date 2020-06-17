package com.zlead.mall;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class ActuatorServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActuatorServerApplication.class, args);
    }
}
