package com.trashfuneral.funeral.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trashfuneral.auth.domain.User;
import com.trashfuneral.auth.repo.UserRepository;
import com.trashfuneral.common.exception.ApiException;
import com.trashfuneral.common.security.UserPrincipal;
import com.trashfuneral.funeral.domain.Funeral;
import com.trashfuneral.funeral.domain.FuneralStatus;
import com.trashfuneral.funeral.domain.ObjectType;
import com.trashfuneral.funeral.dto.AlmanacResponse;
import com.trashfuneral.funeral.dto.CreateFuneralRequest;
import com.trashfuneral.funeral.dto.FuneralResponse;
import com.trashfuneral.funeral.dto.FuneralSummary;
import com.trashfuneral.funeral.dto.IdentificationResponse;
import com.trashfuneral.funeral.dto.PublicCardResponse;
import com.trashfuneral.funeral.dto.UpdateFuneralRequest;
import com.trashfuneral.funeral.repo.FuneralRepository;
import com.trashfuneral.funeral.repo.ObjectTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FuneralService {

    private final FuneralRepository funerals;
    private final ObjectTypeRepository objectTypes;
    private final UserRepository users;
    private final FileStorageService files;
    private final VisionService vision;
    private final AlmanacService almanacService;
    private final ObjectMapper mapper;

    public FuneralService(
            FuneralRepository funerals,
            ObjectTypeRepository objectTypes,
            UserRepository users,
            FileStorageService files,
            VisionService vision,
            AlmanacService almanacService,
            ObjectMapper mapper
    ) {
        this.funerals = funerals;
        this.objectTypes = objectTypes;
        this.users = users;
        this.files = files;
        this.vision = vision;
        this.almanacService = almanacService;
        this.mapper = mapper;
    }

    public IdentificationResponse identify(MultipartFile photo) {
        String photoId = files.store(photo);
        byte[] bytes = files.readBytes(photoId);
        VisionResult result = vision.identify(bytes, photo.getContentType(), photo.getOriginalFilename());
        ObjectType type = objectTypes.findByCode(result.objectTypeCode())
                .orElseGet(() -> objectTypes.findByCode("OTHER").orElseThrow());
        return new IdentificationResponse(
                photoId,
                result.label(),
                type.getCode(),
                type.getNameZh(),
                type.getNameEn(),
                result.confidence(),
                result.mock(),
                type.getEulogyZh(),
                type.getEulogyEn(),
                type.getDefaultMusic(),
                type.getDefaultFlowers()
        );
    }

    @Transactional
    public FuneralResponse create(UserPrincipal principal, CreateFuneralRequest request) {
        User user = requireUser(principal);
        if (!files.exists(request.photoId())) {
            throw ApiException.badRequest("Unknown photoId / 找不到照片");
        }
        ObjectType type = objectTypes.findByCode(request.objectTypeCode())
                .orElseThrow(() -> ApiException.badRequest("Unknown object type / 未知物品类型"));
        if (!RitualCatalog.isMusic(request.musicCode()) || !RitualCatalog.isFlower(request.flowersCode())) {
            throw ApiException.badRequest("Unknown music or flowers / 未知哀乐或献花");
        }
        String locale = normalizeLocale(request.locale());
        LocalDate date = LocalDate.now();
        AlmanacResponse almanac = almanacService.forType(type.getCode(), date);
        Funeral funeral = new Funeral();
        funeral.setUser(user);
        funeral.setObjectType(type);
        funeral.setPhotoId(request.photoId());
        funeral.setIdentifiedLabel(blankTo(request.identifiedLabel(), request.objectName()));
        funeral.setObjectName(request.objectName().trim());
        funeral.setEulogy(request.eulogy().trim());
        funeral.setMusicCode(request.musicCode());
        funeral.setFlowersCode(request.flowersCode());
        funeral.setLocale(locale);
        funeral.setAlmanacJson(writeAlmanac(almanac));
        funeral.setRitualDate(date);
        funeral.setPublicToken(UUID.randomUUID().toString());
        funeral.setStatus(FuneralStatus.DRAFT);
        funerals.save(funeral);
        return toResponse(funeral, almanac);
    }

    @Transactional
    public FuneralResponse update(UserPrincipal principal, Long id, UpdateFuneralRequest request) {
        Funeral funeral = owned(principal, id);
        if (request.objectName() != null && !request.objectName().isBlank()) {
            funeral.setObjectName(request.objectName().trim());
        }
        if (request.eulogy() != null && !request.eulogy().isBlank()) {
            funeral.setEulogy(request.eulogy().trim());
        }
        if (request.musicCode() != null) {
            if (!RitualCatalog.isMusic(request.musicCode())) {
                throw ApiException.badRequest("Unknown music / 未知哀乐");
            }
            funeral.setMusicCode(request.musicCode());
        }
        if (request.flowersCode() != null) {
            if (!RitualCatalog.isFlower(request.flowersCode())) {
                throw ApiException.badRequest("Unknown flowers / 未知献花");
            }
            funeral.setFlowersCode(request.flowersCode());
        }
        if (request.locale() != null) {
            funeral.setLocale(normalizeLocale(request.locale()));
        }
        return toResponse(funeral, readAlmanac(funeral));
    }

    @Transactional
    public FuneralResponse complete(UserPrincipal principal, Long id) {
        Funeral funeral = owned(principal, id);
        funeral.setStatus(FuneralStatus.COMPLETED);
        return toResponse(funeral, readAlmanac(funeral));
    }

    @Transactional(readOnly = true)
    public FuneralResponse get(UserPrincipal principal, Long id) {
        Funeral funeral = owned(principal, id);
        return toResponse(funeral, readAlmanac(funeral));
    }

    @Transactional(readOnly = true)
    public List<FuneralSummary> cemetery(UserPrincipal principal) {
        User user = requireUser(principal);
        return funerals.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public PublicCardResponse publicCard(String token) {
        Funeral funeral = funerals.findByPublicToken(token)
                .orElseThrow(() -> ApiException.notFound("Card not found / 讣告不存在"));
        if (funeral.getStatus() != FuneralStatus.COMPLETED) {
            throw ApiException.notFound("Card not published / 讣告尚未发布");
        }
        ObjectType type = funeral.getObjectType();
        return new PublicCardResponse(
                funeral.getObjectName(),
                funeral.getIdentifiedLabel(),
                type.getNameZh(),
                type.getNameEn(),
                funeral.getEulogy(),
                funeral.getMusicCode(),
                funeral.getFlowersCode(),
                photoUrl(funeral.getPhotoId()),
                readAlmanac(funeral),
                funeral.getLocale(),
                funeral.getRitualDate().toString()
        );
    }

    private Funeral owned(UserPrincipal principal, Long id) {
        User user = requireUser(principal);
        return funerals.findByIdAndUser(id, user)
                .orElseThrow(() -> ApiException.notFound("Funeral not found / 追悼会不存在"));
    }

    private User requireUser(UserPrincipal principal) {
        if (principal == null) {
            throw ApiException.unauthorized("Login required / 请先登录");
        }
        return users.findById(principal.getId()).orElseThrow(() -> ApiException.unauthorized("User gone"));
    }

    private FuneralResponse toResponse(Funeral funeral, AlmanacResponse almanac) {
        ObjectType type = funeral.getObjectType();
        return new FuneralResponse(
                funeral.getId(),
                funeral.getPhotoId(),
                photoUrl(funeral.getPhotoId()),
                funeral.getIdentifiedLabel(),
                type.getCode(),
                type.getNameZh(),
                type.getNameEn(),
                funeral.getObjectName(),
                funeral.getEulogy(),
                funeral.getMusicCode(),
                funeral.getFlowersCode(),
                funeral.getLocale(),
                almanac,
                funeral.getRitualDate(),
                funeral.getPublicToken(),
                "/c/" + funeral.getPublicToken(),
                funeral.getStatus().name(),
                funeral.getCreatedAt(),
                funeral.getUpdatedAt()
        );
    }

    private FuneralSummary toSummary(Funeral funeral) {
        ObjectType type = funeral.getObjectType();
        return new FuneralSummary(
                funeral.getId(),
                photoUrl(funeral.getPhotoId()),
                funeral.getObjectName(),
                type.getCode(),
                type.getNameZh(),
                type.getNameEn(),
                funeral.getStatus().name(),
                funeral.getRitualDate(),
                funeral.getCreatedAt()
        );
    }

    private String writeAlmanac(AlmanacResponse almanac) {
        try {
            return mapper.writeValueAsString(almanac);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private AlmanacResponse readAlmanac(Funeral funeral) {
        try {
            return mapper.readValue(funeral.getAlmanacJson(), AlmanacResponse.class);
        } catch (JsonProcessingException e) {
            return almanacService.forType(funeral.getObjectType().getCode(), funeral.getRitualDate());
        }
    }

    private static String photoUrl(String photoId) {
        return "/api/files/" + photoId;
    }

    private static String normalizeLocale(String locale) {
        if (locale != null && locale.toLowerCase().startsWith("en")) {
            return "en";
        }
        return "zh";
    }

    private static String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
