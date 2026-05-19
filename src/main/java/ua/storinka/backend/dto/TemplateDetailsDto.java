package ua.storinka.backend.dto;

import ua.storinka.backend.entity.Template;

import java.math.BigDecimal;
import java.util.Map;

public record TemplateDetailsDto(
        Long id,
        String key,
        String name,
        String description,
        String thumbnailUrl,
        String category,
        Map<String, Object> schemaJson,
        BigDecimal licensePrice,
        BigDecimal monthlyPrice
) {
    public static TemplateDetailsDto from(Template t) {
        return new TemplateDetailsDto(
                t.getId(),
                t.getKey(),
                t.getName(),
                t.getDescription(),
                t.getThumbnailUrl(),
                t.getCategory(),
                t.getSchemaJson(),
                t.getLicensePrice(),
                t.getMonthlyPrice()
        );
    }
}
