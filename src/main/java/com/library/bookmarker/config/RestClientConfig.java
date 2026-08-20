package com.library.bookmarker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
                .requestInterceptor((request, body, execution) -> {
                    log.info("[RestClient Request URL] {} {}", request.getMethod(), request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }
}
