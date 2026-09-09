package com.trashfuneral.funeral.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFuneralRequest(
        @NotBlank String photoId,
        @NotBlank String objectTypeCode,
        @NotBlank @Size(max = 120) String objectName,
        String identifiedLabel,
        @NotBlank String eulogy,
        @NotBlank String musicCode,
        @NotBlank String flowersCode,
        String locale
) {
}
