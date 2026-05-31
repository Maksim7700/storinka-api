package ua.storinka.backend.dto;

import ua.storinka.backend.entity.UserSite;

import java.util.Map;

/** Shape returned by GET /api/vendors/{subdomain}/site for SSR rendering.
 *  Carries the SEO integration codes too so the frontend can emit the
 *  google-site-verification meta tag and the GA gtag script in one round-trip. */
public record PublicSiteDto(
        String subdomain,
        String templateKey,
        Map<String, Object> contentJson,
        String gscVerification,
        String gaMeasurementId
) {
    public static PublicSiteDto from(UserSite s) {
        return new PublicSiteDto(
                s.getSubdomain(),
                s.getTemplate().getKey(),
                s.getContentJson(),
                s.getGscVerification(),
                s.getGaMeasurementId()
        );
    }
}
