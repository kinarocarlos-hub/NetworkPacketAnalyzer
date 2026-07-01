package com.onesmus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SessionManager sessions;

    public AdminController(SessionManager sessions) {
        this.sessions = sessions;
    }

    @GetMapping("/users")
    public Map<String, Object> users() {
        return Map.of("activeUsers", sessions.all().keySet());
    }

    @GetMapping("/global-stats")
    public Map<String, Object> globalStats() {
        var s = sessions.global().stats();
        return Map.of("total", s.getTotalPackets(), "userCount", sessions.all().size());
    }
}
