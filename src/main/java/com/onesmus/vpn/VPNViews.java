package com.onesmus.vpn;

import java.util.UUID;

record VPNPeerView(UUID id, String ip, String publicKey, String endpoint,
                   CryptoAlgorithm crypto, String nodeName) {}

record VPNSessionView(UUID peerId, String ip, Long up, Long down,
                         String lastHandshake, String status) {}

record VPNSecurityNodeView(UUID id, String name, String region, String endpoint,
                                Integer activePeers, Integer maxPeers) {}