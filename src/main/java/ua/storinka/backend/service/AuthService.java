package ua.storinka.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.storinka.backend.dto.AuthResponse;
import ua.storinka.backend.dto.LoginRequest;
import ua.storinka.backend.dto.RegisterRequest;
import ua.storinka.backend.dto.UserDto;
import ua.storinka.backend.entity.User;
import ua.storinka.backend.enums.AuthIntent;
import ua.storinka.backend.enums.UserStatus;
import ua.storinka.backend.repository.UserRepository;
import ua.storinka.backend.security.GoogleTokenVerifier;
import ua.storinka.backend.security.JwtService;

import java.util.Optional;

/**
 * Auth methods are mutually exclusive: one user has either a password OR a
 * google_id, never both. The rules below enforce this in every entry point:
 * <ul>
 *   <li>{@link #register} rejects an email already linked to Google.</li>
 *   <li>{@link #login} rejects when the account is Google-only.</li>
 *   <li>{@link #googleAuth} rejects when the account has a password.</li>
 * </ul>
 * Rationale: lets the frontend route the user to the correct sign-in path
 * with a clear message, instead of mixing methods silently.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private static final String GOOGLE_ACCOUNT_MSG =
            "Цей email зареєстровано через Google. Увійдіть через Google.";
    private static final String PASSWORD_ACCOUNT_MSG =
            "Цей email зареєстровано через email/пароль. Увійдіть звичайним способом.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        Optional<User> existing = userRepository.findByEmail(req.email());
        if (existing.isPresent()) {
            User u = existing.get();
            if (u.getGoogleId() != null && u.getPassword() == null) {
                // Email already owns a Google-only account — keep methods exclusive.
                throw new ResponseStatusException(HttpStatus.CONFLICT, GOOGLE_ACCOUNT_MSG);
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User user = userRepository.save(User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .phone(req.phone())
                .build());
        try {
            emailVerificationService.issueAndSend(user);
        } catch (MailException e) {
            // Don't block registration if mail service is unreachable;
            // user can be resent a verification email later.
            log.warn("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
        }
        return new AuthResponse(jwtService.generate(user.getId(), user.getEmail()), UserDto.from(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account suspended");
        }
        // Account is Google-only — surface that explicitly so the UI can hint
        // "use the Google button" instead of showing a generic credentials error.
        if (user.getGoogleId() != null && user.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, GOOGLE_ACCOUNT_MSG);
        }
        if (user.getPassword() == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new AuthResponse(jwtService.generate(user.getId(), user.getEmail()), UserDto.from(user));
    }

    @Transactional
    public AuthResponse googleAuth(String idToken, AuthIntent intent) {
        // Default to LOGIN when intent is missing — silently creating accounts
        // from the login screen is exactly the bug this field is here to fix.
        AuthIntent effective = intent != null ? intent : AuthIntent.LOGIN;

        GoogleIdToken.Payload payload = googleTokenVerifier.verify(idToken);
        String email = payload.getEmail();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google account has no email");
        }
        String googleId = payload.getSubject();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");

        Optional<User> maybeUser = userRepository.findByEmail(email);
        User user;
        if (maybeUser.isPresent()) {
            user = maybeUser.get();
            // Email is bound to a password account — mutual exclusivity blocks
            // Google sign-in even though Google verified the email.
            if (user.getPassword() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, PASSWORD_ACCOUNT_MSG);
            }
            if (user.getStatus() == UserStatus.SUSPENDED) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account suspended");
            }
            // Defensive: if google_id drifted (unlikely — `sub` is stable), update.
            if (user.getGoogleId() == null || !user.getGoogleId().equals(googleId)) {
                user.setGoogleId(googleId);
            }
            // For an existing Google account both intents resolve to "log in":
            // a returning user clicking Register on a different machine still
            // expects to land in their account, not see an error.
        } else {
            if (effective != AuthIntent.REGISTER) {
                // Login screen + no account → don't auto-register. Tell the
                // frontend so it can prompt the user to switch to /register.
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Акаунт з таким Google email не знайдено. Зареєструйтесь.");
            }
            // First-time Google sign-in via /register — create the account.
            // Google already verified the email, so we skip email-verification.
            user = userRepository.save(User.builder()
                    .email(email)
                    .fullName(name)
                    .avatarUrl(picture)
                    .googleId(googleId)
                    .emailVerified(true)
                    .build());
        }
        return new AuthResponse(jwtService.generate(user.getId(), user.getEmail()), UserDto.from(user));
    }
}
