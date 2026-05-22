package ua.storinka.backend.dto;

import ua.storinka.backend.entity.Template;
import ua.storinka.backend.entity.UserSite;
import ua.storinka.backend.enums.SiteStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record SiteSummaryDto(
        Long id,
        String subdomain,
        SiteStatus status,
        Long templateId,
        String templateKey,
        String templateName,
        String templateDescription,
        String templateCategory,
        String templateThumbnailUrl,
        BigDecimal licensePrice,
        BigDecimal monthlyPrice,
        Instant createdAt,
        Instant updatedAt
) {
    public static SiteSummaryDto from(UserSite s) {
        Template t = s.getTemplate();
        return new SiteSummaryDto(
                s.getId(),
                s.getSubdomain(),
                s.getStatus(),
                t.getId(),
                t.getKey(),
                t.getName(),
                t.getDescription(),
                t.getCategory(),
                t.getThumbnailUrl(),
                t.getLicensePrice(),
                t.getMonthlyPrice(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
