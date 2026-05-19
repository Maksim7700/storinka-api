package ua.storinka.backend.dto;

import ua.storinka.backend.entity.Template;

import java.math.BigDecimal;

public record TemplateSummaryDto(
        Long id,
        String key,
        String name,
        String description,
        String thumbnailUrl,
        String category,
        BigDecimal licensePrice,
        BigDecimal monthlyPrice
) {
    public static TemplateSummaryDto from(Template t) {
        return new TemplateSummaryDto(
                t.getId(),
                t.getKey(),
                t.getName(),
                t.getDescription(),
                t.getThumbnailUrl(),
                t.getCategory(),
                t.getLicensePrice(),
                t.getMonthlyPrice()
        );
    }
}
