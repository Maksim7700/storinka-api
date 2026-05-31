package ua.storinka.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Owner-supplied integration codes for SEO / analytics tooling. Both fields
 * are optional — sending an empty string clears the value.
 *
 * Validation is permissive on purpose: Google occasionally changes the exact
 * format of these strings, and we'd rather store something the owner copied
 * from their dashboard than reject a slightly-off shape.
 */
public record UpdateSeoSettingsRequest(
        @Size(max = 128, message = "GSC verification code занадто довгий")
        @Pattern(
                regexp = "^[A-Za-z0-9_\\-]*$",
                message = "GSC verification code містить недопустимі символи"
        )
        String gscVerification,

        @Size(max = 32, message = "GA measurement ID занадто довгий")
        @Pattern(
                regexp = "^[A-Za-z0-9\\-]*$",
                message = "GA measurement ID містить недопустимі символи"
        )
        String gaMeasurementId
) {
}
