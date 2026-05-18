package ua.storinka.backend.dto;

public record AuthResponse(String token, UserDto user) {}
