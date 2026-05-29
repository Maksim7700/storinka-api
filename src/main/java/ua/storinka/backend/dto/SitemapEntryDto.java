package ua.storinka.backend.dto;

import ua.storinka.backend.entity.UserSite;

import java.time.Instant;

/** Minimal shape consumed by the frontend's sitemap.xml builder. */
public record SitemapEntryDto(
        String subdomain,
        Instant updatedAt
) {
    public static SitemapEntryDto from(UserSite s) {
        return new SitemapEntryDto(s.getSubdomain(), s.getUpdatedAt());
    }
}
