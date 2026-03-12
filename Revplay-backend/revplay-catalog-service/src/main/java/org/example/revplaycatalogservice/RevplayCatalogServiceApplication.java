package org.example.revplaycatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class
RevplayCatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevplayCatalogServiceApplication.class, args);
    }

}
