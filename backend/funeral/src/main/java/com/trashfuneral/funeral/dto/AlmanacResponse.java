package com.trashfuneral.funeral.dto;

import java.util.List;

public record AlmanacResponse(
        String date,
        String objectTypeCode,
        String stemBranchZh,
        String stemBranchEn,
        String directionZh,
        String directionEn,
        String whereToThrowZh,
        String whereToThrowEn,
        List<String> suitableZh,
        List<String> suitableEn,
        List<String> avoidZh,
        List<String> avoidEn,
        String luckyHourZh,
        String luckyHourEn,
        String verseZh,
        String verseEn,
        String disclaimerZh,
        String disclaimerEn
) {
}
