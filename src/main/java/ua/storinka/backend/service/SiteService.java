package ua.storinka.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.storinka.backend.dto.CheckSubdomainResponse;
import ua.storinka.backend.dto.CreateSiteRequest;
import ua.storinka.backend.dto.PublicSiteDto;
import ua.storinka.backend.dto.SiteDetailsDto;
import ua.storinka.backend.dto.SiteSummaryDto;
import ua.storinka.backend.dto.SitemapEntryDto;
import ua.storinka.backend.dto.UpdateContentRequest;
import ua.storinka.backend.dto.UpdateSubdomainRequest;
import ua.storinka.backend.entity.Template;
import ua.storinka.backend.entity.User;
import ua.storinka.backend.entity.UserSite;
import ua.storinka.backend.enums.Role;
import ua.storinka.backend.enums.SiteStatus;
import ua.storinka.backend.repository.TemplateRepository;
import ua.storinka.backend.repository.UserSiteRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SiteService {

    private static final Pattern SUBDOMAIN_PATTERN =
            Pattern.compile("^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$");

    private static final Set<String> RESERVED_SUBDOMAINS = Set.of(
            "www", "admin", "api", "mail", "static",
            "cdn", "app", "support", "billing"
    );

    private final UserSiteRepository siteRepository;
    private final TemplateRepository templateRepository;

    @Transactional(readOnly = true)
    public List<SiteSummaryDto> listMine(User currentUser) {
        return siteRepository.findByUserIdWithTemplate(currentUser.getId())
                .stream()
                .map(SiteSummaryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SiteDetailsDto getOwned(Long id, User currentUser) {
        UserSite site = siteRepository
                .findByIdAndUserIdWithTemplate(id, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Site not found"));
        return SiteDetailsDto.from(site);
    }

    @Transactional
    public SiteDetailsDto create(CreateSiteRequest req, User currentUser) {
        if (!SUBDOMAIN_PATTERN.matcher(req.subdomain()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid subdomain format");
        }
        if (RESERVED_SUBDOMAINS.contains(req.subdomain())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Subdomain is reserved");
        }
        if (siteRepository.existsBySubdomain(req.subdomain())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Subdomain is already taken");
        }

        Template template = templateRepository.findById(req.templateId())
                .filter(Template::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Template not found"));

        UserSite site = siteRepository.save(UserSite.builder()
                .user(currentUser)
                .template(template)
                .subdomain(req.subdomain())
                .contentJson(req.contentJson() != null ? req.contentJson() : new HashMap<>())
                .build());
        return SiteDetailsDto.from(site);
    }

    @Transactional
    public SiteDetailsDto updateSubdomain(Long id, UpdateSubdomainRequest req, User currentUser) {
        UserSite site = siteRepository
                .findByIdAndUserIdWithTemplate(id, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Site not found"));

        // Subdomain is only mutable while the site is in DRAFT. Once paid (ACTIVE)
        // or paused/inactive, the subdomain is part of the published identity and
        // changing it would break inbound links and indexed URLs.
        if (site.getStatus() != SiteStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Subdomain can only be changed while the site is a draft");
        }

        String newSubdomain = req.subdomain();
        if (newSubdomain.equals(site.getSubdomain())) {
            // No-op — return current state without hitting the uniqueness check.
            return SiteDetailsDto.from(site);
        }
        if (!SUBDOMAIN_PATTERN.matcher(newSubdomain).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid subdomain format");
        }
        if (RESERVED_SUBDOMAINS.contains(newSubdomain)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Subdomain is reserved");
        }
        if (siteRepository.existsBySubdomain(newSubdomain)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Subdomain is already taken");
        }

        site.setSubdomain(newSubdomain);
        return SiteDetailsDto.from(site);
    }

    @Transactional
    public SiteDetailsDto updateContent(Long id, UpdateContentRequest req, User currentUser) {
        UserSite site = siteRepository
                .findByIdAndUserIdWithTemplate(id, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Site not found"));
        site.setContentJson(req.contentJson());
        // @UpdateTimestamp + JPA dirty checking refreshes updated_at on flush.
        return SiteDetailsDto.from(site);
    }

    @Transactional
    public SiteDetailsDto publish(Long id, User currentUser) {
        UserSite site = siteRepository
                .findByIdAndUserIdWithTemplate(id, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Site not found"));
        // Payment flow is deferred; publish just flips status. When billing
        // arrives this method will gate ACTIVE behind a paid subscription.
        site.setStatus(SiteStatus.ACTIVE);
        return SiteDetailsDto.from(site);
    }

    @Transactional
    public SiteDetailsDto unpublish(Long id, User currentUser) {
        UserSite site = siteRepository
                .findByIdAndUserIdWithTemplate(id, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Site not found"));
        // Voluntary pause keeps the paid subscription intact, so the owner
        // can resume without paying again. Going back to DRAFT would lose
        // that semantic distinction.
        site.setStatus(SiteStatus.SUSPENDED);
        return SiteDetailsDto.from(site);
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        UserSite site = siteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Site not found"));
        boolean isOwner = site.getUser().getId().equals(currentUser.getId());
        if (!isOwner && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your site");
        }
        siteRepository.delete(site);
    }

    @Transactional(readOnly = true)
    public PublicSiteDto findPublicBySubdomain(String subdomain) {
        return siteRepository.findActiveBySubdomainWithTemplate(subdomain)
                .map(PublicSiteDto::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Site not found or not published"));
    }

    /** All ACTIVE sites for sitemap.xml. Public — no auth required. */
    @Transactional(readOnly = true)
    public List<SitemapEntryDto> listSitemapEntries() {
        return siteRepository.findAllActive()
                .stream()
                .map(SitemapEntryDto::from)
                .toList();
    }

    public CheckSubdomainResponse checkSubdomain(String value) {
        if (value == null || !SUBDOMAIN_PATTERN.matcher(value).matches()) {
            return new CheckSubdomainResponse(false,
                    "Невірний формат. Тільки a-z, 0-9, дефіс; 3-63 символи.");
        }
        if (RESERVED_SUBDOMAINS.contains(value)) {
            return new CheckSubdomainResponse(false,
                    "Цей піддомен зарезервовано системою.");
        }
        boolean available = !siteRepository.existsBySubdomain(value);
        return new CheckSubdomainResponse(
                available,
                available ? "" : "Цей піддомен вже зайнятий.");
    }
}
