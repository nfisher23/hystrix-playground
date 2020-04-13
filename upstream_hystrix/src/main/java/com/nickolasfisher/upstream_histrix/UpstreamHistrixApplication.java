package com.nickolasfisher.upstream_histrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
@EnableCircuitBreaker
public class UpstreamHistrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpstreamHistrixApplication.class, args);
    }

}
