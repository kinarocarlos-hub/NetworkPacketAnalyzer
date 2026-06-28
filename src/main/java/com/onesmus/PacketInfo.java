package com.onesmus;

public record PacketInfo(
        String protocol,
        String sourceIp,
        String destinationIp,
        int sourcePort,
        int destinationPort,
        int length) {

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
