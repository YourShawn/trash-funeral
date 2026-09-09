package com.trashfuneral.common.api;

public record ApiResponse<T>(boolean ok, T data, String message) {

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
