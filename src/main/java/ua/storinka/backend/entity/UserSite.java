package ua.storinka.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import ua.storinka.backend.enums.SiteStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_sites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false, unique = true, length = 63)
    private String subdomain;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_json", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> contentJson = new HashMap<>();

    @Column(name = "custom_domain", length = 255)
    private String customDomain;

    /** Google Search Console verification code (HTML-tag method). Rendered
     *  as `<meta name="google-site-verification" content="...">` so the owner
     *  can claim the property in GSC and see search analytics. */
    @Column(name = "gsc_verification", length = 128)
    private String gscVerification;

    /** Google Analytics 4 measurement ID, e.g. `G-XXXXXXXXXX`. When present
     *  the public site loads the gtag script and reports pageviews. */
    @Column(name = "ga_measurement_id", length = 32)
    private String gaMeasurementId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Builder.Default
    private SiteStatus status = SiteStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
