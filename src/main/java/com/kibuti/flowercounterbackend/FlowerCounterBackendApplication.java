package com.kibuti.flowercounterbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class FlowerCounterBackendApplication {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }


    public static void main(String[] args) {
        SpringApplication.run(FlowerCounterBackendApplication.class, args);
    }

}
