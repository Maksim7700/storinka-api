package ua.storinka.backend.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Verifies a Google ID token (the JWT credential GIS returns to the frontend).
 *
 * <p>Validation done by {@link GoogleIdTokenVerifier#verify(String)}:
 * <ul>
 *   <li>JWT signature against Google's published JWKS</li>
 *   <li>{@code iss} is {@code accounts.google.com} or {@code https://accounts.google.com}</li>
 *   <li>{@code aud} matches our configured client ID</li>
 *   <li>token not expired</li>
 * </ul>
 * Any failure yields {@code 401}.
 */
@Component
@Slf4j
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;
    private final boolean configured;

    public GoogleTokenVerifier(@Value("${app.google.client-id:}") String clientId) {
        this.configured = !clientId.isBlank();
        if (configured) {
            this.verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();
        } else {
            this.verifier = null;
            log.warn("app.google.client-id is not set — POST /api/auth/google will return 503");
        }
    }

    public GoogleIdToken.Payload verify(String idToken) {
        if (!configured) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Google sign-in is not configured on this server");
        }
        try {
            GoogleIdToken parsed = verifier.verify(idToken);
            if (parsed == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google token");
            }
            return parsed.getPayload();
        } catch (GeneralSecurityException | IOException e) {
            log.warn("Google token verification failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Google token verification failed");
        }
    }
}
