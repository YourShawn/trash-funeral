package com.trashfuneral.funeral.dto;

public record IdentificationResponse(
        String photoId,
        String label,
        String objectTypeCode,
        String objectTypeNameZh,
        String objectTypeNameEn,
        double confidence,
        boolean mock,
        String suggestedEulogyZh,
        String suggestedEulogyEn,
        String suggestedMusic,
        String suggestedFlowers
) {
}
