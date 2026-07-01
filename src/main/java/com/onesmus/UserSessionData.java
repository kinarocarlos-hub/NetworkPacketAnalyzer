package com.onesmus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class UserSessionData {

    private static final int CAP = 1000;

    private final BlockingQueue<PacketData> packets = new LinkedBlockingQueue<>(CAP);
    private final PacketStatistics stats = new PacketStatistics();
    private final Map<String, Integer> ipCounts = new ConcurrentHashMap<>();

    public void add(PacketData data) {
        if (!packets.offer(data)) {
            packets.poll();
            packets.offer(data);
        }
        stats.recordPacket(data.sourceIp(), "TCP".equals(data.protocol()), "UDP".equals(data.protocol()));
        ipCounts.merge(data.sourceIp(), 1, Integer::sum);
    }

    public List<PacketData> recent(int limit) {
        int from = Math.max(0, packets.size() - limit);
        return new ArrayList<>(packets).subList(from, packets.size());
    }

    public PacketStatistics stats() { return stats; }

    public Map<String, Integer> ipCounts() { return ipCounts; }
}
