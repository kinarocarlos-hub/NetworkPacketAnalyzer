package com.onesmus;

public class PacketInfo {

    private String protocol;
    private String sourceIp;
    private String destinationIp;
    private int sourcePort;
    private int destinationPort;
    private int length;

    public PacketInfo(
            String protocol,
            String sourceIp,
            String destinationIp,
            int sourcePort,
            int destinationPort,
            int length) {

        this.protocol = protocol;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.length = length;
    }

    @Override
    public String toString() {

        return protocol + " | "
                + sourceIp + ":" + sourcePort
                + " -> "
                + destinationIp + ":" + destinationPort
                + " | "
                + length + " bytes";
    }
}