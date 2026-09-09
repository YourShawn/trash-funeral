package com.trashfuneral.funeral.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "object_types")
public class ObjectType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(name = "name_zh", nullable = false, length = 80)
    private String nameZh;

    @Column(name = "name_en", nullable = false, length = 80)
    private String nameEn;

    @Lob
    @Column(name = "eulogy_zh", nullable = false, columnDefinition = "TEXT")
    private String eulogyZh;

    @Lob
    @Column(name = "eulogy_en", nullable = false, columnDefinition = "TEXT")
    private String eulogyEn;

    @Column(name = "default_music", nullable = false, length = 64)
    private String defaultMusic;

    @Column(name = "default_flowers", nullable = false, length = 64)
    private String defaultFlowers;

    @Lob
    @Column(name = "throw_hint_zh", nullable = false, columnDefinition = "TEXT")
    private String throwHintZh;

    @Lob
    @Column(name = "throw_hint_en", nullable = false, columnDefinition = "TEXT")
    private String throwHintEn;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getEulogyZh() {
        return eulogyZh;
    }

    public void setEulogyZh(String eulogyZh) {
        this.eulogyZh = eulogyZh;
    }

    public String getEulogyEn() {
        return eulogyEn;
    }

    public void setEulogyEn(String eulogyEn) {
        this.eulogyEn = eulogyEn;
    }

    public String getDefaultMusic() {
        return defaultMusic;
    }

    public void setDefaultMusic(String defaultMusic) {
        this.defaultMusic = defaultMusic;
    }

    public String getDefaultFlowers() {
        return defaultFlowers;
    }

    public void setDefaultFlowers(String defaultFlowers) {
        this.defaultFlowers = defaultFlowers;
    }

    public String getThrowHintZh() {
        return throwHintZh;
    }

    public void setThrowHintZh(String throwHintZh) {
        this.throwHintZh = throwHintZh;
    }

    public String getThrowHintEn() {
        return throwHintEn;
    }

    public void setThrowHintEn(String throwHintEn) {
        this.throwHintEn = throwHintEn;
    }
}
