package com.momin.fydp_sync.dto;

public record RegisterRequest(
        String name,
        String username,
        String email,
        String password,
        String role
) {}