package com.trashfuneral.funeral.dto;

import java.util.List;

public record CatalogResponse(
        List<ObjectTypeResponse> objectTypes,
        List<CatalogItem> music,
        List<CatalogItem> flowers
) {
}
