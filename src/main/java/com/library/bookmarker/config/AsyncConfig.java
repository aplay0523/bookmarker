package com.library.bookmarker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean(name = "bookApiExecutor")
    public Executor bookApiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(12);
        executor.setMaxPoolSize(36);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ThreadPool-book-");
        executor.initialize();
        return executor;
    }
}
