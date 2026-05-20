package ua.storinka.backend.dto;

import ua.storinka.backend.entity.UserSite;
import ua.storinka.backend.enums.SiteStatus;

import java.time.Instant;
import java.util.Map;

public record SiteDetailsDto(
        Long id,
        String subdomain,
        String customDomain,
        SiteStatus status,
        Long templateId,
        String templateKey,
        String templateName,
        String templateThumbnailUrl,
        Map<String, Object> contentJson,
        Instant createdAt,
        Instant updatedAt
) {
    public static SiteDetailsDto from(UserSite s) {
        return new SiteDetailsDto(
                s.getId(),
                s.getSubdomain(),
                s.getCustomDomain(),
                s.getStatus(),
                s.getTemplate().getId(),
                s.getTemplate().getKey(),
                s.getTemplate().getName(),
                s.getTemplate().getThumbnailUrl(),
                s.getContentJson(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
