package com.trashfuneral.funeral.domain;

import com.trashfuneral.auth.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "funerals")
public class Funeral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "object_type_id", nullable = false)
    private ObjectType objectType;

    @Column(name = "photo_id", nullable = false, length = 48)
    private String photoId;

    @Column(name = "identified_label", nullable = false, length = 160)
    private String identifiedLabel;

    @Column(name = "object_name", nullable = false, length = 120)
    private String objectName;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String eulogy;

    @Column(name = "music_code", nullable = false, length = 64)
    private String musicCode;

    @Column(name = "flowers_code", nullable = false, length = 64)
    private String flowersCode;

    @Column(nullable = false, length = 8)
    private String locale;

    @Lob
    @Column(name = "almanac_json", nullable = false, columnDefinition = "TEXT")
    private String almanacJson;

    @Column(name = "ritual_date", nullable = false)
    private LocalDate ritualDate;

    @Column(name = "public_token", nullable = false, unique = true, length = 36)
    private String publicToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private FuneralStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = FuneralStatus.DRAFT;
        }
        if (locale == null) {
            locale = "zh";
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getIdentifiedLabel() {
        return identifiedLabel;
    }

    public void setIdentifiedLabel(String identifiedLabel) {
        this.identifiedLabel = identifiedLabel;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getEulogy() {
        return eulogy;
    }

    public void setEulogy(String eulogy) {
        this.eulogy = eulogy;
    }

    public String getMusicCode() {
        return musicCode;
    }

    public void setMusicCode(String musicCode) {
        this.musicCode = musicCode;
    }

    public String getFlowersCode() {
        return flowersCode;
    }

    public void setFlowersCode(String flowersCode) {
        this.flowersCode = flowersCode;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getAlmanacJson() {
        return almanacJson;
    }

    public void setAlmanacJson(String almanacJson) {
        this.almanacJson = almanacJson;
    }

    public LocalDate getRitualDate() {
        return ritualDate;
    }

    public void setRitualDate(LocalDate ritualDate) {
        this.ritualDate = ritualDate;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public FuneralStatus getStatus() {
        return status;
    }

    public void setStatus(FuneralStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
