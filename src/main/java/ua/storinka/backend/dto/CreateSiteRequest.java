package ua.storinka.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record CreateSiteRequest(
        @NotNull Long templateId,

        @NotBlank
        @Size(min = 3, max = 63)
        @Pattern(
                regexp = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$",
                message = "Subdomain must contain only a-z, 0-9, hyphen; 3-63 chars; can't start or end with a hyphen"
        )
        String subdomain,

        Map<String, Object> contentJson
) {}
