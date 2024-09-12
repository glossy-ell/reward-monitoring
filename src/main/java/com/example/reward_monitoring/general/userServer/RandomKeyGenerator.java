package com.example.reward_monitoring.general.userServer;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomKeyGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomKey(int length) {
        // Ensure that the length is valid (positive and even)
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }

        // Convert desired length to byte length (Base64 encoding increases size)
        int byteLength = (int) Math.ceil(length * 3.0 / 4.0);
        byte[] key = new byte[byteLength];
        random.nextBytes(key);

        // Generate Base64 encoded string without padding
        String base64Key = Base64.getUrlEncoder().withoutPadding().encodeToString(key);

        // Trim the result to the exact length required
        return base64Key.substring(0, Math.min(length, base64Key.length()));
    }
}