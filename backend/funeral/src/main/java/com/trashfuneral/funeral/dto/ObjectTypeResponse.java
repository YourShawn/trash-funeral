package com.trashfuneral.funeral.dto;

public record ObjectTypeResponse(
        String code,
        String nameZh,
        String nameEn,
        String defaultMusic,
        String defaultFlowers
) {
}
