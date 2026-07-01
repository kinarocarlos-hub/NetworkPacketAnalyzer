package com.onesmus;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final Map<String, UserSessionData> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> ipMap = new ConcurrentHashMap<>();

    public SessionManager() {
        ipMap.put("user1", "192.168.1.100");
    }

    public UserSessionData get(String username) {
        return sessions.computeIfAbsent(username, k -> new UserSessionData());
    }

    public String ipFor(String username) {
        return ipMap.get(username);
    }

    public void bindIp(String username, String ip) {
        ipMap.put(username, ip);
    }

    public UserSessionData global() {
        return new UserSessionData();
    }

    public Map<String, UserSessionData> all() {
        return sessions;
    }
}
