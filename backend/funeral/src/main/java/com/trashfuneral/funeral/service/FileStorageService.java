package com.trashfuneral.funeral.service;

import com.trashfuneral.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.root = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest("Photo is required / 请上传照片");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(contentType)) {
            throw ApiException.badRequest("Only JPEG, PNG, WebP or GIF photos / 仅支持 JPEG、PNG、WebP、GIF");
        }
        String ext = switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
        String id = UUID.randomUUID().toString();
        Path dest = root.resolve(id + ext);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store photo", e);
        }
        return id + ext;
    }

    public Resource load(String photoId) {
        Path path = resolve(photoId);
        if (!Files.exists(path)) {
            throw ApiException.notFound("Photo not found / 照片不存在");
        }
        return new FileSystemResource(path);
    }

    public byte[] readBytes(String photoId) {
        try {
            return Files.readAllBytes(resolve(photoId));
        } catch (IOException e) {
            throw ApiException.notFound("Photo not found / 照片不存在");
        }
    }

    public MediaType mediaType(String photoId) {
        String lower = photoId.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        return MediaType.IMAGE_JPEG;
    }

    public boolean exists(String photoId) {
        return Files.exists(resolve(photoId));
    }

    private Path resolve(String photoId) {
        if (photoId == null || photoId.contains("..") || photoId.contains("/") || photoId.contains("\\")) {
            throw ApiException.badRequest("Invalid photo id");
        }
        return root.resolve(photoId).normalize();
    }
}
