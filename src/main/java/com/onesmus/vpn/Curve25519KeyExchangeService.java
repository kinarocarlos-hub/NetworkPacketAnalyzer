package com.onesmus.vpn;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class Curve25519KeyExchangeService implements KeyExchangeService {
    private static final String PROVIDER_NAME = "BC";
    private static final String ALGORITHM = "XDH";
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    @Override
    public KeyPair generateKeyPair(CryptoAlgorithm algorithm) {
        if (algorithm != CryptoAlgorithm.CURVE25519) {
            throw new IllegalArgumentException("This service only supports CURVE25519");
        }
        
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER_NAME);
            kpg.initialize(new ECGenParameterSpec("X25519"));
            return kpg.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate key pair", e);
        }
    }
    
    @Override
    public byte[] deriveSharedSecret(KeyPair privateKey, PublicKey publicKey) {
        try {
            javax.crypto.KeyAgreement ka = javax.crypto.KeyAgreement.getInstance(ALGORITHM, PROVIDER_NAME);
            ka.init(privateKey.getPrivate());
            ka.doPhase(publicKey, true);
            return ka.generateSecret();
        } catch (Exception e) {
            throw new RuntimeException("Failed to derive shared secret", e);
        }
    }
    
    @Override
    public String encodePublicKey(PublicKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    @Override
    public String encodePrivateKey(KeyPair keyPair) {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }
}