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
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(4);
        ts.setThreadNamePrefix("vpn-scan-");
        return ts;
    }
}