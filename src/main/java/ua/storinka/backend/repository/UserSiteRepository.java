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
}
