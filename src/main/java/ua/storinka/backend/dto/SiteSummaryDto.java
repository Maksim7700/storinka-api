package ua.storinka.backend.dto;

import ua.storinka.backend.entity.UserSite;
import ua.storinka.backend.enums.SiteStatus;

import java.time.Instant;

public record SiteSummaryDto(
        Long id,
        String subdomain,
        SiteStatus status,
        Long templateId,
        String templateKey,
        String templateName,
        String templateThumbnailUrl,
        Instant createdAt,
        Instant updatedAt
) {
    public static SiteSummaryDto from(UserSite s) {
        return new SiteSummaryDto(
                s.getId(),
                s.getSubdomain(),
                s.getStatus(),
                s.getTemplate().getId(),
                s.getTemplate().getKey(),
                s.getTemplate().getName(),
                s.getTemplate().getThumbnailUrl(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
