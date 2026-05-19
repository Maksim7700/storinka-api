package ua.storinka.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ua.storinka.backend.dto.TemplateDetailsDto;
import ua.storinka.backend.dto.TemplateSummaryDto;
import ua.storinka.backend.entity.Template;
import ua.storinka.backend.repository.TemplateRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    @Transactional(readOnly = true)
    public List<TemplateSummaryDto> listActive() {
        return templateRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(TemplateSummaryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TemplateDetailsDto getByKey(String key) {
        Template t = templateRepository.findByKey(key)
                .filter(Template::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Template not found"));
        return TemplateDetailsDto.from(t);
    }
}
