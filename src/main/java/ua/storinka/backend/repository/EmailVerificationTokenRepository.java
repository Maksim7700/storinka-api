package ua.storinka.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.storinka.backend.entity.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);
}
