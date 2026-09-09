package com.trashfuneral.auth.dto;

public record UserResponse(Long id, String username, String email, String displayName) {
}
