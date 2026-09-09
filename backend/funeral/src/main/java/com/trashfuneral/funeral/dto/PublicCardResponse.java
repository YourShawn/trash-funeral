package com.trashfuneral.funeral.dto;

public record PublicCardResponse(
        String objectName,
        String identifiedLabel,
        String objectTypeNameZh,
        String objectTypeNameEn,
        String eulogy,
        String musicCode,
        String flowersCode,
        String photoUrl,
        AlmanacResponse almanac,
        String locale,
        String ritualDate
) {
}
