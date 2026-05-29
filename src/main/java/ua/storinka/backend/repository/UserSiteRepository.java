package ua.storinka.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.storinka.backend.entity.UserSite;

import java.util.List;
import java.util.Optional;

public interface UserSiteRepository extends JpaRepository<UserSite, Long> {

    @Query("""
            SELECT s FROM UserSite s
            JOIN FETCH s.template
            WHERE s.user.id = :userId
            ORDER BY s.updatedAt DESC
            """)
    List<UserSite> findByUserIdWithTemplate(@Param("userId") Long userId);

    @Query("""
            SELECT s FROM UserSite s
            JOIN FETCH s.template
            WHERE s.id = :id AND s.user.id = :userId
            """)
    Optional<UserSite> findByIdAndUserIdWithTemplate(
            @Param("id") Long id,
            @Param("userId") Long userId);

    boolean existsBySubdomain(String subdomain);

    @org.springframework.data.jpa.repository.Query("""
            SELECT s FROM UserSite s
            JOIN FETCH s.template
            WHERE s.subdomain = :subdomain
              AND s.status = ua.storinka.backend.enums.SiteStatus.ACTIVE
            """)
    Optional<UserSite> findActiveBySubdomainWithTemplate(
            @org.springframework.data.repository.query.Param("subdomain") String subdomain);

    /** All ACTIVE sites for sitemap.xml generation. Returns only the fields
     *  needed by the sitemap (subdomain + updatedAt) — no JOIN FETCH to keep
     *  it cheap when the table grows. */
    @Query("""
            SELECT s FROM UserSite s
            WHERE s.status = ua.storinka.backend.enums.SiteStatus.ACTIVE
            ORDER BY s.updatedAt DESC
            """)
    List<UserSite> findAllActive();
}
