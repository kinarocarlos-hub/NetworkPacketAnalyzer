package com.onesmus;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarlosService {

    // ── Intent keywords ──────────────────────────────────────────────────────
    private static final Map<String, List<String>> INTENTS = Map.of(
        "threat",     List.of("threat","attack","hack","malware","suspicious","danger","scan","intrusion","exploit","malicious"),
        "talkers",    List.of("talker","top","most","active","busiest","who","which ip","bandwidth","heavy"),
        "protocol",   List.of("protocol","http","https","dns","tcp","udp","ssh","ftp","smtp","dhcp","what is","explain"),
        "anomaly",    List.of("anomaly","unusual","strange","weird","odd","unexpected","spike","surge","abnormal"),
        "stats",      List.of("stat","total","count","how many","number","summary","overview","report"),
        "status",     List.of("status","running","capturing","active","online","working","capture"),
        "help",       List.of("help","what can","capabilities","features","commands","what do you do")
    );

    // ── Known malicious / suspicious ports ───────────────────────────────────
    private static final Set<Integer> SUSPICIOUS_PORTS = Set.of(
        4444, 5555, 6666, 7777, 8888, 9999,  // common backdoor ports
        1337, 31337,                           // hacker ports
        12345, 54321,                          // trojan ports
        3389,                                  // RDP brute force target
        23,                                    // Telnet (unencrypted)
        445, 139,                              // SMB (WannaCry etc.)
        1433, 3306, 5432                       // exposed databases
    );

    private static final Set<String> PRIVATE_RANGES = Set.of("10.", "192.168.", "172.16.", "172.17.", "172.18.");

    // ── Conversation history (last 6 exchanges) ───────────────────────────────
    private final LinkedList<String[]> history = new LinkedList<>();

    public boolean hasApiKey() { return true; } // always ready — no key needed

    public String chat(String userMessage, PacketStatistics stats, List<PacketData> packets) {
        String msg = userMessage.toLowerCase().trim();
        String intent = classifyIntent(msg);
        String response = generateResponse(intent, msg, stats, packets);

        // Keep history
        history.addLast(new String[]{ userMessage, response });
        if (history.size() > 6) history.removeFirst();

        return response;
    }

    // ── Intent classifier ────────────────────────────────────────────────────
    private String classifyIntent(String msg) {
        Map<String, Integer> scores = new HashMap<>();
        for (var entry : INTENTS.entrySet()) {
            int score = 0;
            for (String kw : entry.getValue()) {
                if (msg.contains(kw)) score += kw.length(); // longer match = higher score
            }
            if (score > 0) scores.put(entry.getKey(), score);
        }
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("general");
    }

    // ── Response generator ───────────────────────────────────────────────────
    private String generateResponse(String intent, String msg,
                                    PacketStatistics stats, List<PacketData> packets) {
        return switch (intent) {
            case "threat"   -> analyzeThreat(stats, packets);
            case "talkers"  -> analyzeTopTalkers(stats);
            case "protocol" -> analyzeProtocols(msg, stats, packets);
            case "anomaly"  -> detectAnomalies(stats, packets);
            case "stats"    -> buildSummary(stats);
            case "status"   -> buildStatus(stats);
            case "help"     -> buildHelp();
            default         -> analyzeGeneral(msg, stats, packets);
        };
    }

    // ── Threat analysis ──────────────────────────────────────────────────────
    private String analyzeThreat(PacketStatistics stats, List<PacketData> packets) {
        List<String> threats = new ArrayList<>();
        Map<String, Integer> portCounts = new HashMap<>();
        Map<String, Set<Integer>> ipPorts = new HashMap<>();
        int externalCount = 0;
        int suspiciousCount = 0;

        for (PacketData p : packets) {
            // Track port scanning (one IP hitting many ports)
            ipPorts.computeIfAbsent(p.sourceIp(), k -> new HashSet<>()).add(p.destPort());

            // Count suspicious ports
            if (SUSPICIOUS_PORTS.contains(p.destPort()) || SUSPICIOUS_PORTS.contains(p.sourcePort())) {
                suspiciousCount++;
                portCounts.merge(String.valueOf(p.destPort()), 1, Integer::sum);
            }

            // Count external IPs
            String src = p.sourceIp();
            boolean isPrivate = PRIVATE_RANGES.stream().anyMatch(src::startsWith);
            if (!isPrivate && !src.startsWith("127.")) externalCount++;
        }

        // Port scan detection
        ipPorts.entrySet().stream()
                .filter(e -> e.getValue().size() > 10)
                .forEach(e -> threats.add("🔴 Possible port scan from **" + e.getKey() +
                        "** — hitting " + e.getValue().size() + " different ports"));

        // Suspicious ports
        if (suspiciousCount > 0) {
            portCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(3)
                    .forEach(e -> threats.add("⚠️ Traffic on suspicious port **" + e.getKey() +
                            "** (" + e.getValue() + " packets)"));
        }

        // High external traffic
        if (packets.size() > 0) {
            double externalRatio = (double) externalCount / packets.size();
            if (externalRatio > 0.8) {
                threats.add("⚠️ High external traffic ratio: " +
                        String.format("%.0f%%", externalRatio * 100) + " of packets going outside your network");
            }
        }

        // DNS flood detection
        long dnsCount = packets.stream().filter(p -> "DNS".equals(p.protocol())).count();
        if (dnsCount > packets.size() * 0.4 && dnsCount > 10) {
            threats.add("⚠️ Unusually high DNS traffic (" + dnsCount + " packets) — possible DNS tunneling or exfiltration");
        }

        // Unencrypted traffic
        long httpCount = packets.stream().filter(p -> "HTTP".equals(p.protocol())).count();
        if (httpCount > 5) {
            threats.add("💡 " + httpCount + " unencrypted HTTP packets detected — consider HTTPS");
        }

        if (threats.isEmpty()) {
            return "✅ No immediate threats detected in your current traffic.\n\n" +
                   "Analyzed " + packets.size() + " packets — everything looks normal. " +
                   "I'll keep watching for anomalies.\n\n— Carlos";
        }

        return "🔍 **Threat Analysis Results:**\n\n" +
               String.join("\n", threats) +
               "\n\nAnalyzed " + packets.size() + " packets. Stay vigilant!\n\n— Carlos";
    }

    // ── Top talkers analysis ─────────────────────────────────────────────────
    private String analyzeTopTalkers(PacketStatistics stats) {
        Map<String, Integer> talkers = stats.getTopTalkers();
        if (talkers.isEmpty()) {
            return "No traffic data yet. Start packet capture to see top talkers.\n\n— Carlos";
        }

        StringBuilder sb = new StringBuilder("📊 **Top Network Talkers:**\n\n");
        int rank = 1;
        int total = talkers.values().stream().mapToInt(Integer::intValue).sum();

        for (var entry : talkers.entrySet()) {
            String ip = entry.getKey();
            int count = entry.getValue();
            double pct = total > 0 ? (double) count / total * 100 : 0;
            boolean isPrivate = PRIVATE_RANGES.stream().anyMatch(ip::startsWith);
            String tag = isPrivate ? "🏠 local" : "🌐 external";

            sb.append(rank++).append(". **").append(ip).append("** (").append(tag).append(")\n");
            sb.append("   ↳ ").append(count).append(" packets (")
              .append(String.format("%.1f%%", pct)).append(" of traffic)\n");
        }

        sb.append("\n— Carlos");
        return sb.toString();
    }

    // ── Protocol analysis ────────────────────────────────────────────────────
    private String analyzeProtocols(String msg, PacketStatistics stats, List<PacketData> packets) {
        // Check if asking about a specific protocol
        String[] protocols = {"http", "https", "dns", "tcp", "udp", "ssh", "ftp", "smtp", "dhcp"};
        for (String proto : protocols) {
            if (msg.contains(proto)) return explainProtocol(proto.toUpperCase(), packets);
        }

        // General protocol breakdown
        long total = stats.getTotalPackets();
        if (total == 0) return "No packets captured yet. Start capture to see protocol breakdown.\n\n— Carlos";

        Map<String, Long> protoCounts = packets.stream()
                .collect(Collectors.groupingBy(PacketData::protocol, Collectors.counting()));

        StringBuilder sb = new StringBuilder("📡 **Protocol Breakdown:**\n\n");
        protoCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> {
                    double pct = (double) e.getValue() / packets.size() * 100;
                    sb.append("• **").append(e.getKey()).append("**: ")
                      .append(e.getValue()).append(" packets (")
                      .append(String.format("%.1f%%", pct)).append(")\n");
                });

        sb.append("\nTCP: ").append(stats.getTcpPackets())
          .append(" | UDP: ").append(stats.getUdpPackets())
          .append(" | Other: ").append(stats.getOtherPackets());
        sb.append("\n\n— Carlos");
        return sb.toString();
    }

    private String explainProtocol(String proto, List<PacketData> packets) {
        long count = packets.stream().filter(p -> proto.equals(p.protocol())).count();
        String explanation = switch (proto) {
            case "HTTP"  -> "**HTTP** (Port 80) — Unencrypted web traffic. ⚠️ Avoid sending sensitive data over HTTP.";
            case "HTTPS" -> "**HTTPS** (Port 443) — Encrypted web traffic using TLS/SSL. ✅ Safe for sensitive data.";
            case "DNS"   -> "**DNS** (Port 53) — Translates domain names to IP addresses. High DNS traffic can indicate tunneling.";
            case "TCP"   -> "**TCP** — Reliable connection-oriented protocol. Used by HTTP, HTTPS, SSH, FTP etc.";
            case "UDP"   -> "**UDP** — Fast connectionless protocol. Used by DNS, streaming, gaming, VoIP.";
            case "SSH"   -> "**SSH** (Port 22) — Encrypted remote shell access. ✅ Secure. Watch for brute force attempts.";
            case "FTP"   -> "**FTP** (Port 21) — File transfer. ⚠️ Unencrypted — use SFTP instead.";
            case "SMTP"  -> "**SMTP** (Port 25) — Email sending protocol. Unusual SMTP traffic may indicate spam bots.";
            case "DHCP"  -> "**DHCP** (Ports 67/68) — Automatically assigns IP addresses on your network.";
            default      -> "**" + proto + "** — Network protocol detected in your traffic.";
        };
        return explanation + "\n\nCurrently seeing **" + count + "** " + proto + " packets in your traffic.\n\n— Carlos";
    }

    // ── Anomaly detection ────────────────────────────────────────────────────
    private String detectAnomalies(PacketStatistics stats, List<PacketData> packets) {
        List<String> anomalies = new ArrayList<>();
        if (packets.isEmpty()) return "No packets to analyze yet. Start capture first.\n\n— Carlos";

        // Packet size anomaly
        OptionalDouble avgSize = packets.stream().mapToInt(PacketData::bytes).average();
        if (avgSize.isPresent()) {
            long largePackets = packets.stream().filter(p -> p.bytes() > 1400).count();
            long tinyPackets  = packets.stream().filter(p -> p.bytes() < 40).count();
            if (largePackets > packets.size() * 0.5)
                anomalies.add("📦 Unusually large packets (" + largePackets + " packets > 1400B) — possible data exfiltration");
            if (tinyPackets > packets.size() * 0.6)
                anomalies.add("📦 Unusually many tiny packets (" + tinyPackets + " packets < 40B) — possible ping flood or keepalive abuse");
        }

        // Protocol ratio anomaly
        long total = stats.getTotalPackets();
        if (total > 50) {
            double udpRatio = (double) stats.getUdpPackets() / total;
            double otherRatio = (double) stats.getOtherPackets() / total;
            if (udpRatio > 0.7)
                anomalies.add("🔁 Very high UDP ratio (" + String.format("%.0f%%", udpRatio * 100) + ") — possible UDP flood or streaming abuse");
            if (otherRatio > 0.3)
                anomalies.add("❓ High 'other' protocol traffic (" + String.format("%.0f%%", otherRatio * 100) + ") — unusual non-TCP/UDP activity");
        }

        // Single IP dominance
        Map<String, Integer> talkers = stats.getTopTalkers();
        if (!talkers.isEmpty()) {
            int topCount = talkers.values().iterator().next();
            if (total > 0 && (double) topCount / total > 0.6)
                anomalies.add("📍 Single IP dominates traffic: **" + talkers.keySet().iterator().next() +
                        "** accounts for " + String.format("%.0f%%", (double) topCount / total * 100) + " of all packets");
        }

        if (anomalies.isEmpty())
            return "✅ Traffic patterns look normal — no anomalies detected.\n\nAvg packet size: " +
                   String.format("%.0f", avgSize.orElse(0)) + " bytes across " + packets.size() + " packets.\n\n— Carlos";

        return "🔍 **Anomalies Detected:**\n\n" + String.join("\n", anomalies) + "\n\n— Carlos";
    }

    // ── Stats summary ────────────────────────────────────────────────────────
    private String buildSummary(PacketStatistics stats) {
        long total = stats.getTotalPackets();
        if (total == 0) return "No packets captured yet. Hit **Start** to begin monitoring.\n\n— Carlos";

        double tcpPct   = (double) stats.getTcpPackets()   / total * 100;
        double udpPct   = (double) stats.getUdpPackets()   / total * 100;
        double otherPct = (double) stats.getOtherPackets() / total * 100;

        return "📈 **Traffic Summary:**\n\n" +
               "• Total packets: **" + total + "**\n" +
               "• TCP: **" + stats.getTcpPackets() + "** (" + String.format("%.1f%%", tcpPct) + ")\n" +
               "• UDP: **" + stats.getUdpPackets() + "** (" + String.format("%.1f%%", udpPct) + ")\n" +
               "• Other: **" + stats.getOtherPackets() + "** (" + String.format("%.1f%%", otherPct) + ")\n" +
               "• Top talkers: **" + stats.getTopTalkers().size() + "** unique IPs\n\n— Carlos";
    }

    // ── Status ───────────────────────────────────────────────────────────────
    private String buildStatus(PacketStatistics stats) {
        boolean active = stats.getTotalPackets() > 0;
        return (active ? "✅ NetPulse is **active** and capturing traffic.\n" : "⏸️ No packets captured yet — capture may be stopped.\n") +
               "Total packets seen: **" + stats.getTotalPackets() + "**\n\n— Carlos";
    }

    // ── Help ─────────────────────────────────────────────────────────────────
    private String buildHelp() {
        return "👋 I'm **Carlos**, your AI network assistant. Here's what I can do:\n\n" +
               "• 🔴 **Threat detection** — \"Check for threats\" or \"Any attacks?\"\n" +
               "• 📊 **Top talkers** — \"Who's using the most bandwidth?\"\n" +
               "• 📡 **Protocol analysis** — \"Explain DNS\" or \"Show protocol breakdown\"\n" +
               "• 🔍 **Anomaly detection** — \"Anything unusual in my traffic?\"\n" +
               "• 📈 **Statistics** — \"Give me a summary\" or \"How many packets?\"\n" +
               "• ✅ **Status** — \"Is capture running?\"\n\n" +
               "I work 100% offline — no internet needed! 🚀\n\n— Carlos";
    }

    // ── General fallback ─────────────────────────────────────────────────────
    private String analyzeGeneral(String msg, PacketStatistics stats, List<PacketData> packets) {
        // Try to answer based on context
        if (stats.getTotalPackets() == 0)
            return "No traffic data yet. Start packet capture and then ask me to analyze your network! 🚀\n\n— Carlos";

        // Default: give a quick overview + suggestion
        return "📡 **Current Network Snapshot:**\n\n" +
               "• " + stats.getTotalPackets() + " total packets captured\n" +
               "• " + stats.getTopTalkers().size() + " active IPs\n" +
               "• TCP/UDP split: " + stats.getTcpPackets() + " / " + stats.getUdpPackets() + "\n\n" +
               "Try asking me:\n" +
               "• \"Check for threats\"\n" +
               "• \"Show top talkers\"\n" +
               "• \"Detect anomalies\"\n\n— Carlos";
    }
}
