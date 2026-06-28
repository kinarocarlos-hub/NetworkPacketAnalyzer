package com.onesmus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SessionManager sessionManager;

    public AdminController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        return Map.of(
                "activeUsers", sessionManager.getAllSessions().keySet(),
                "totalPacketsGlobal", sessionManager.getGlobalSession().getStatistics().getTotalPackets()
        );
    }

    @GetMapping("/global-stats")
    public Map<String, Object> getGlobalStats() {
        var stats = sessionManager.getGlobalSession().getStatistics();
        return Map.of(
                "total", stats.getTotalPackets(),
                "userCount", sessionManager.getAllSessions().size()
        );
    }
}
