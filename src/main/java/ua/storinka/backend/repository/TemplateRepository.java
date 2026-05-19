package ua.storinka.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.storinka.backend.entity.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByIsActiveTrueOrderByCreatedAtDesc();

    Optional<Template> findByKey(String key);
}
