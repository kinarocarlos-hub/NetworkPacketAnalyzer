package com.onesmus.vpn;

import java.security.KeyPair;
import java.security.PublicKey;

public interface KeyExchangeService {
    KeyPair generateKeyPair(CryptoAlgorithm algorithm);
    
    byte[] deriveSharedSecret(KeyPair privateKey, PublicKey publicKey);
    
    String encodePublicKey(PublicKey key);
    
    String encodePrivateKey(KeyPair keyPair);
    
    default boolean isCryptoSupported(CryptoAlgorithm algorithm) {
        return switch (algorithm) {
            case CURVE25519 -> true;
            case KYBER, DILITHIUM, FALCON, HYBRID -> false;
        };
    }
}