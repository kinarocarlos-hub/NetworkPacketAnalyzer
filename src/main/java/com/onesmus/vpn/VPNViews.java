package com.onesmus.vpn;

import java.util.UUID;

record VPNPeerView(UUID id, String ipAddress, String publicKey, String endpoint,
                          CryptoAlgorithm crypto, String nodeName) {}

record VPNSessionView(UUID peerId, String ipAddress, Long bytesUp, Long bytesDown,
                            String lastHandshake, String status) {}

record VPNSecurityNodeView(UUID id, String name, String region, String endpoint,
                                 Integer activePeers, Integer maxPeers) {}