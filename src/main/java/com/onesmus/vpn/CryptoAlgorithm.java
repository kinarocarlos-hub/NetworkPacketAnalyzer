package com.onesmus.vpn;

public enum CryptoAlgorithm {
    CURVE25519,    // Current WireGuard standard
    KYBER,          // Post-quantum KEM
    DILITHIUM,      // Post-quantum signature
    FALCON,         // Alternative post-quantum signature
    HYBRID          // Hybrid classical + post-quantum
}