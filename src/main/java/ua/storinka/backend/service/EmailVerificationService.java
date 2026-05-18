package ua.storinka.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.storinka.backend.entity.EmailVerificationToken;
import ua.storinka.backend.entity.User;
import ua.storinka.backend.repository.EmailVerificationTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private static final long TOKEN_TTL_HOURS = 24;

    private final EmailVerificationTokenRepository tokenRepo;
    private final EmailService emailService;

    @Transactional
    public void issueAndSend(User user) {
        EmailVerificationToken token = tokenRepo.save(EmailVerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(TOKEN_TTL_HOURS, ChronoUnit.HOURS))
                .build());
        emailService.sendVerificationEmail(user, token.getToken());
    }

    @Transactional
    public void verify(String token) {
        EmailVerificationToken evt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));
        if (evt.getUsedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already used");
        }
        if (evt.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
        }
        evt.setUsedAt(Instant.now());
        evt.getUser().setEmailVerified(true);
    }
}
