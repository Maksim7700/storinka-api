package ua.storinka.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UpdateContentRequest(@NotNull Map<String, Object> contentJson) {}
