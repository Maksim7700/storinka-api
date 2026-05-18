package ua.storinka.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.storinka.backend.entity.User;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.url.backend}")
    private String backendUrl;

    public void sendVerificationEmail(User user, String token) {
        String link = backendUrl + "/api/auth/verify-email?token=" + token;
        String greeting = user.getFullName() != null && !user.getFullName().isBlank()
                ? user.getFullName()
                : "користувач";

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(user.getEmail());
        msg.setSubject("Підтвердіть свою email-адресу — Storinka");
        msg.setText(
                "Привіт, " + greeting + "!\n\n" +
                "Підтвердіть свою email-адресу за посиланням:\n" +
                link + "\n\n" +
                "Посилання дійсне 24 години.\n\n" +
                "Якщо ви не реєструвались на Storinka — проігноруйте цей лист."
        );
        mailSender.send(msg);
    }
}
