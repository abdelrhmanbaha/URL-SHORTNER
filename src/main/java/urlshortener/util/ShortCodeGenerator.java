package urlshortener.util;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Component
public class ShortCodeGenerator {

    public String generate(String fullUrl) {
        try {
            // Step 1: Create SHA-256 hasher
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Step 2: Hash the URL into bytes
            byte[] hashBytes = digest.digest(
                    fullUrl.getBytes(StandardCharsets.UTF_8)
            );

            // Step 3: Convert bytes to hex string
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }

            // Step 4: Take only first 8 characters
            return hex.toString().substring(0, 8);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate short code", e);
        }
    }
}