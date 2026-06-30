package com.onesmus.vpn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class VPNConfig {
    
    @Bean
    public KeyExchangeService keyExchangeService() {
        return new Curve25519KeyExchangeService();
    }
    
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("vpn-scanner-");
        return scheduler;
    }
}