package org.example.revplayconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class RevplayConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RevplayConfigServerApplication.class, args);
    }

}
