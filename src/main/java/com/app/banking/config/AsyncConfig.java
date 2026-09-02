package com.app.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.*;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                cores * 2,           // corePoolSize: Keeps enough threads ready for fast I/O response
                cores * 4,                      // maximumPoolSize: Scales up under sudden heavy spikes
                60L, TimeUnit.SECONDS,          // keepAliveTime: Destroys idle spike threads after 1 minute
                new LinkedBlockingQueue<>(500), // bounded queue: Protects memory from OutOfMemoryErrors
                new ThreadPoolExecutor.CallerRunsPolicy() // rejection handler: Throttles clients if system is maxed out
        );
        return executor;
    }
}
