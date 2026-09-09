package com.trashfuneral.funeral.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class OpenAiVisionService implements VisionService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiVisionService.class);
    private static final Set<String> TYPES = Set.of(
            "ELECTRONICS", "CLOTHES", "FOOD", "PAPER", "TOY", "PLANT", "COSMETICS", "FURNITURE", "PACKAGING", "OTHER"
    );

    private final MockVisionService mock;
    private final ObjectMapper mapper;
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public OpenAiVisionService(
            MockVisionService mock,
            ObjectMapper mapper,
            @Value("${app.openai.api-key:}") String apiKey,
            @Value("${app.openai.model:gpt-4o-mini}") String model
    ) {
        this.mock = mock;
        this.mapper = mapper;
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    @Override
    public VisionResult identify(byte[] imageBytes, String contentType, String filename) {
        if (!isConfigured()) {
            return mock.identify(imageBytes, filename);
        }
        try {
            String mime = contentType == null || contentType.isBlank() ? "image/jpeg" : contentType;
            String dataUrl = "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
            String prompt = """
                    Identify the main discarded/household object in this photo for a humorous memorial app.
                    Return STRICT JSON only, no markdown:
                    {"label":"short bilingual or English name","objectType":"ELECTRONICS|CLOTHES|FOOD|PAPER|TOY|PLANT|COSMETICS|FURNITURE|PACKAGING|OTHER","confidence":0.0}
                    objectType must be one of those codes.
                    """;
            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", 200,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of("type", "text", "text", prompt),
                                    Map.of("type", "image_url", "image_url", Map.of("url", dataUrl))
                            )
                    ))
            );
            String raw = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(body)
                    .retrieve()
                    .body(String.class);
            JsonNode root = mapper.readTree(raw);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            JsonNode parsed = mapper.readTree(extractJson(content));
            String type = parsed.path("objectType").asText("OTHER").toUpperCase(Locale.ROOT);
            if (!TYPES.contains(type)) {
                type = "OTHER";
            }
            String label = parsed.path("label").asText("mysterious object");
            double confidence = parsed.path("confidence").asDouble(0.7);
            return new VisionResult(label, type, Math.min(1.0, Math.max(0.0, confidence)), false);
        } catch (Exception ex) {
            log.warn("OpenAI vision failed, falling back to mock: {}", ex.getMessage());
            VisionResult fallback = mock.identify(imageBytes, filename);
            return new VisionResult(fallback.label(), fallback.objectTypeCode(), fallback.confidence(), true);
        }
    }

    private static String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        return "{}";
    }
}
