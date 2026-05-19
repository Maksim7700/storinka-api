package ua.storinka.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.storinka.backend.dto.TemplateDetailsDto;
import ua.storinka.backend.dto.TemplateSummaryDto;
import ua.storinka.backend.service.TemplateService;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public List<TemplateSummaryDto> list() {
        return templateService.listActive();
    }

    @GetMapping("/{key}")
    public TemplateDetailsDto get(@PathVariable String key) {
        return templateService.getByKey(key);
    }
}
