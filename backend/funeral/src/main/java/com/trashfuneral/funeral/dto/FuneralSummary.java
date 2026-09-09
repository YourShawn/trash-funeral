package com.trashfuneral.funeral.dto;

import java.time.Instant;
import java.time.LocalDate;

public record FuneralSummary(
        Long id,
        String photoUrl,
        String objectName,
        String objectTypeCode,
        String objectTypeNameZh,
        String objectTypeNameEn,
        String status,
        LocalDate ritualDate,
        Instant createdAt
) {
}
