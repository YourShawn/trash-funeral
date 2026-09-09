package com.trashfuneral.funeral.dto;

import jakarta.validation.constraints.Size;

public record UpdateFuneralRequest(
        @Size(max = 120) String objectName,
        String eulogy,
        String musicCode,
        String flowersCode,
        String locale
) {
}
