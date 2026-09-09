package com.trashfuneral.funeral.web;

import com.trashfuneral.common.api.ApiResponse;
import com.trashfuneral.funeral.dto.CatalogResponse;
import com.trashfuneral.funeral.dto.ObjectTypeResponse;
import com.trashfuneral.funeral.repo.ObjectTypeRepository;
import com.trashfuneral.funeral.service.RitualCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final ObjectTypeRepository objectTypes;

    public CatalogController(ObjectTypeRepository objectTypes) {
        this.objectTypes = objectTypes;
    }

    @GetMapping
    public ApiResponse<CatalogResponse> catalog() {
        var types = objectTypes.findAll().stream()
                .map(t -> new ObjectTypeResponse(t.getCode(), t.getNameZh(), t.getNameEn(), t.getDefaultMusic(), t.getDefaultFlowers()))
                .toList();
        return ApiResponse.of(new CatalogResponse(types, RitualCatalog.MUSIC, RitualCatalog.FLOWERS));
    }
}
