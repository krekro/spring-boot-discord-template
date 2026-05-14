package com.krekro.discordbot.service;

import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DiscordVerificationService {

    private final Ed25519PublicKeyParameters publicKeyParameters;

    public DiscordVerificationService(@Value("${discord.public-key}") String publicKeyHex) {
        byte[] publicKeyBytes = Hex.decode(publicKeyHex);
        this.publicKeyParameters = new Ed25519PublicKeyParameters(publicKeyBytes, 0);
    }

    public boolean isValidRequest(String signatureHex, String timestamp, String body) {
        try {
            byte[] signature = Hex.decode(signatureHex);
            byte[] message = (timestamp + body).getBytes();

            Ed25519Signer signer = new Ed25519Signer();
            signer.init(false, publicKeyParameters);
            signer.update(message, 0, message.length);

            return signer.verifySignature(signature);
        } catch (Exception e) {
            return false;
        }
    }
}