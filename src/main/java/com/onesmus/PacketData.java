package com.onesmus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PacketData(
        String timestamp,
        String protocol,
        String sourceIp,
        int sourcePort,
        String destIp,
        int destPort,
        int bytes) {

    public PacketData(String protocol, String sourceIp, int sourcePort,
            String destIp, int destPort, int bytes) {
        this(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                protocol, sourceIp, sourcePort, destIp, destPort, bytes);
    }

    @Override
    public String toString() {
        return protocol + " | " + sourceIp + ":" + sourcePort +
                " -> " + destIp + ":" + destPort + " | " + bytes + " bytes";
    }
}
