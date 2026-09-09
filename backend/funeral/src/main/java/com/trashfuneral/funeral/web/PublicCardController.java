package com.trashfuneral.funeral.web;

import com.trashfuneral.common.api.ApiResponse;
import com.trashfuneral.funeral.dto.PublicCardResponse;
import com.trashfuneral.funeral.service.FuneralService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/cards")
public class PublicCardController {

    private final FuneralService funeralService;

    public PublicCardController(FuneralService funeralService) {
        this.funeralService = funeralService;
    }

    @GetMapping("/{token}")
    public ApiResponse<PublicCardResponse> card(@PathVariable String token) {
        return ApiResponse.of(funeralService.publicCard(token));
    }
}
