package ua.storinka.backend.dto;

import jakarta.validation.constraints.NotBlank;
import ua.storinka.backend.enums.AuthIntent;

/**
 * Body for POST /api/auth/google. The frontend obtains this ID token from
 * Google Identity Services (https://developers.google.com/identity/gsi/web)
 * — we don't do the OAuth code exchange ourselves.
 *
 * <p>{@code intent} tells the backend which page the user came from:
 * {@link AuthIntent#LOGIN} rejects an unknown Google account with 404,
 * {@link AuthIntent#REGISTER} creates it. Defaults to LOGIN (safer — won't
 * silently auto-register if the frontend forgets to send the field).
 */
public record GoogleAuthRequest(
        @NotBlank String idToken,
        AuthIntent intent
) {}
