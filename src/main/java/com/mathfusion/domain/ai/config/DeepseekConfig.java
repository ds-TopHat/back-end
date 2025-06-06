package com.mathfusion.domain.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DeepseekConfig {
    @Value("${deepseek.api.url}")
    private String deepSeekBaseUrl;

    @Bean
    public WebClient deepSeekWebClient() {
        return WebClient.builder()
                .baseUrl(deepSeekBaseUrl)
                .build();
    }
}
