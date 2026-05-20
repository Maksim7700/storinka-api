package ua.storinka.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.storinka.backend.dto.PublicSiteDto;
import ua.storinka.backend.service.SiteService;

/**
 * Public-facing endpoints that anonymous visitors hit when they open a
 * published site (subdomain.storinka.ua). No auth — SecurityConfig has
 * /api/vendors/** in permitAll.
 */
@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final SiteService siteService;

    @GetMapping("/{subdomain}/site")
    public PublicSiteDto getSite(@PathVariable String subdomain) {
        return siteService.findPublicBySubdomain(subdomain);
    }
}
