package com.onesmus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PacketData {
    private String timestamp;
    private String protocol;
    private String sourceIp;
    private int sourcePort;
    private String destIp;
    private int destPort;
    private int bytes;

    public PacketData(String protocol, String sourceIp, int sourcePort,
            String destIp, int destPort, int bytes) {
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        this.protocol = protocol;
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.destIp = destIp;
        this.destPort = destPort;
        this.bytes = bytes;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getDestIp() {
        return destIp;
    }

    public int getDestPort() {
        return destPort;
    }

    public int getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return protocol + " | " + sourceIp + ":" + sourcePort +
                " -> " + destIp + ":" + destPort + " | " + bytes + " bytes";
    }
}
