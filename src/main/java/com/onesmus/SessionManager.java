package com.onesmus;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, UserSessionData> userSessions = new ConcurrentHashMap<>();
    private final Map<String, String> userIpMap = new ConcurrentHashMap<>(); // username -> IP
    private final UserSessionData globalSession = new UserSessionData();

    public SessionManager() {
        // Sample mappings
        userIpMap.put("user1", "192.168.1.100");
    }

    public UserSessionData getSession(String username) {
        return userSessions.computeIfAbsent(username, k -> new UserSessionData());
    }

    public String getUserIp(String username) {
        return userIpMap.get(username);
    }

    public void bindUserIp(String username, String ip) {
        userIpMap.put(username, ip);
    }

    public UserSessionData getGlobalSession() {
        return globalSession;
    }

    public Map<String, UserSessionData> getAllSessions() {
        return userSessions;
    }
}
