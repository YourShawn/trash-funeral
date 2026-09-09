package com.trashfuneral.funeral.dto;

import java.time.Instant;
import java.time.LocalDate;

public record FuneralResponse(
        Long id,
        String photoId,
        String photoUrl,
        String identifiedLabel,
        String objectTypeCode,
        String objectTypeNameZh,
        String objectTypeNameEn,
        String objectName,
        String eulogy,
        String musicCode,
        String flowersCode,
        String locale,
        AlmanacResponse almanac,
        LocalDate ritualDate,
        String publicToken,
        String publicUrl,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
