package com.mathfusion.domain.deepseek.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${deepseek.api.url}")
    private String deepSeekBaseUrl;

    @Bean
    public WebClient deepSeekWebClient() {
        return WebClient.builder()
                .baseUrl(deepSeekBaseUrl)
                .build();
    }
}
