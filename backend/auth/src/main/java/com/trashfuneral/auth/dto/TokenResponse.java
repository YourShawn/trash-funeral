package com.trashfuneral.auth.dto;

public record TokenResponse(String token, String tokenType, UserResponse user) {
}
