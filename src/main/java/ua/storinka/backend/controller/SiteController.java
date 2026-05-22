package ua.storinka.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.storinka.backend.dto.CheckSubdomainResponse;
import ua.storinka.backend.dto.CreateSiteRequest;
import ua.storinka.backend.dto.SiteDetailsDto;
import ua.storinka.backend.dto.SiteSummaryDto;
import ua.storinka.backend.dto.UpdateContentRequest;
import ua.storinka.backend.dto.UpdateSubdomainRequest;
import ua.storinka.backend.entity.User;
import ua.storinka.backend.service.SiteService;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @GetMapping
    public List<SiteSummaryDto> list(@AuthenticationPrincipal User currentUser) {
        return siteService.listMine(currentUser);
    }

    @GetMapping("/check-subdomain")
    public CheckSubdomainResponse checkSubdomain(@RequestParam String value) {
        return siteService.checkSubdomain(value);
    }

    @GetMapping("/{id}")
    public SiteDetailsDto get(@PathVariable Long id,
                              @AuthenticationPrincipal User currentUser) {
        return siteService.getOwned(id, currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SiteDetailsDto create(@Valid @RequestBody CreateSiteRequest req,
                                  @AuthenticationPrincipal User currentUser) {
        return siteService.create(req, currentUser);
    }

    @PutMapping("/{id}/content")
    public SiteDetailsDto updateContent(@PathVariable Long id,
                                         @Valid @RequestBody UpdateContentRequest req,
                                         @AuthenticationPrincipal User currentUser) {
        return siteService.updateContent(id, req, currentUser);
    }

    @PatchMapping("/{id}/subdomain")
    public SiteDetailsDto updateSubdomain(@PathVariable Long id,
                                          @Valid @RequestBody UpdateSubdomainRequest req,
                                          @AuthenticationPrincipal User currentUser) {
        return siteService.updateSubdomain(id, req, currentUser);
    }

    @PatchMapping("/{id}/publish")
    public SiteDetailsDto publish(@PathVariable Long id,
                                   @AuthenticationPrincipal User currentUser) {
        return siteService.publish(id, currentUser);
    }

    @PatchMapping("/{id}/unpublish")
    public SiteDetailsDto unpublish(@PathVariable Long id,
                                     @AuthenticationPrincipal User currentUser) {
        return siteService.unpublish(id, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal User currentUser) {
        siteService.delete(id, currentUser);
    }
}
