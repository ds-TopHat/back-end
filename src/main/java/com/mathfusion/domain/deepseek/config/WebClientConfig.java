package com.mathfusion.domain.deepseek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient deepSeekWebClient() {
        return WebClient.builder()
                .baseUrl("http://ec2-54-180-102-105.ap-northeast-2.compute.amazonaws.com:8000")
                .build();
    }
}
