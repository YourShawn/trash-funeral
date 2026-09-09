package com.trashfuneral.funeral.web;

import com.trashfuneral.common.api.ApiResponse;
import com.trashfuneral.common.security.UserPrincipal;
import com.trashfuneral.funeral.dto.CreateFuneralRequest;
import com.trashfuneral.funeral.dto.FuneralResponse;
import com.trashfuneral.funeral.dto.FuneralSummary;
import com.trashfuneral.funeral.dto.IdentificationResponse;
import com.trashfuneral.funeral.dto.UpdateFuneralRequest;
import com.trashfuneral.funeral.service.FuneralService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/funerals")
public class FuneralController {

    private final FuneralService funeralService;

    public FuneralController(FuneralService funeralService) {
        this.funeralService = funeralService;
    }

    @PostMapping(value = "/identify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<IdentificationResponse> identify(@RequestPart("photo") MultipartFile photo) {
        return ApiResponse.of(funeralService.identify(photo));
    }

    @PostMapping
    public ApiResponse<FuneralResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateFuneralRequest request
    ) {
        return ApiResponse.of(funeralService.create(principal, request));
    }

    @GetMapping
    public ApiResponse<List<FuneralSummary>> cemetery(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.of(funeralService.cemetery(principal));
    }

    @GetMapping("/{id}")
    public ApiResponse<FuneralResponse> get(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        return ApiResponse.of(funeralService.get(principal, id));
    }

    @PutMapping("/{id}")
    public ApiResponse<FuneralResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateFuneralRequest request
    ) {
        return ApiResponse.of(funeralService.update(principal, id, request));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<FuneralResponse> complete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        return ApiResponse.of(funeralService.complete(principal, id));
    }
}
