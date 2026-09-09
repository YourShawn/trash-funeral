package com.trashfuneral.funeral.web;

import com.trashfuneral.common.api.ApiResponse;
import com.trashfuneral.funeral.dto.AlmanacResponse;
import com.trashfuneral.funeral.service.AlmanacService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/almanac")
public class AlmanacController {

    private final AlmanacService almanacService;

    public AlmanacController(AlmanacService almanacService) {
        this.almanacService = almanacService;
    }

    @GetMapping
    public ApiResponse<AlmanacResponse> almanac(
            @RequestParam(defaultValue = "OTHER") String objectType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate d = date == null ? LocalDate.now() : date;
        return ApiResponse.of(almanacService.forType(objectType, d));
    }
}
