package ua.storinka.backend.dto;

import ua.storinka.backend.entity.UserSite;

import java.util.Map;

/** Shape returned by GET /api/vendors/{subdomain}/site for SSR rendering. */
public record PublicSiteDto(
        String subdomain,
        String templateKey,
        Map<String, Object> contentJson
) {
    public static PublicSiteDto from(UserSite s) {
        return new PublicSiteDto(
                s.getSubdomain(),
                s.getTemplate().getKey(),
                s.getContentJson()
        );
    }
}
