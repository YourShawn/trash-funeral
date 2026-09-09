package com.trashfuneral.funeral.web;

import com.trashfuneral.funeral.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService files;

    public FileController(FileStorageService files) {
        this.files = files;
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<Resource> get(@PathVariable String photoId) {
        Resource resource = files.load(photoId);
        return ResponseEntity.ok()
                .contentType(files.mediaType(photoId))
                .header(HttpHeaders.CACHE_CONTROL, CacheControl.maxAge(Duration.ofDays(7)).cachePublic().getHeaderValue())
                .body(resource);
    }
}
