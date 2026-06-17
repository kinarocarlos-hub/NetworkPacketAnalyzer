package com.onesmus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
        
        // Start packet capture automatically
        PacketCaptureService captureService = context.getBean(PacketCaptureService.class);
        captureService.startCapture();
        
        System.out.println("\n=================================");
        System.out.println(" Network Packet Analyzer Started ");
        System.out.println(" Web Dashboard: http://localhost:8080");
        System.out.println(" API: http://localhost:8080/api");
        System.out.println("=================================\n");
    }
}