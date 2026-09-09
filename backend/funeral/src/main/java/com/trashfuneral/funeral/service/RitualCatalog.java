package com.trashfuneral.funeral.service;

import com.trashfuneral.funeral.dto.CatalogItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RitualCatalog {

    public static final List<CatalogItem> MUSIC = List.of(
            new CatalogItem("canon_can", "卡农（罐头版）", "Pachelbel's Canned Canon"),
            new CatalogItem("moon_expired", "月亮代表我的心（过期版）", "The Moon Represents My Expired Heart"),
            new CatalogItem("chopin_trash", "垃圾桶夜曲", "Trashinata in C-minor"),
            new CatalogItem("farewell_bin", "送你离开（送到桶边）", "See You at the Bin"),
            new CatalogItem("silent_beep", "电子默哀哔哔", "Electronic Moment of Beep"),
            new CatalogItem("bubble_wrap", "气泡膜安魂曲", "Bubble-Wrap Requiem"),
            new CatalogItem("receipt_choir", "小票唱诗班", "Receipt Paper Choir")
    );

    public static final List<CatalogItem> FLOWERS = List.of(
            new CatalogItem("white_mum", "白菊一束", "White chrysanthemums"),
            new CatalogItem("plastic_forever", "塑料花（永不凋零）", "Plastic blooms (never wilt)"),
            new CatalogItem("sticky_wreath", "便利贴花圈", "Sticky-note wreath"),
            new CatalogItem("incense_usb", "USB 电子香", "USB electric incense"),
            new CatalogItem("onion_tears", "洋葱催泪花", "Onion tear bouquet"),
            new CatalogItem("barcode_lily", "条码百合", "Barcode lilies")
    );

    private static final Map<String, CatalogItem> MUSIC_MAP = MUSIC.stream()
            .collect(Collectors.toMap(CatalogItem::code, i -> i));
    private static final Map<String, CatalogItem> FLOWER_MAP = FLOWERS.stream()
            .collect(Collectors.toMap(CatalogItem::code, i -> i));

    private RitualCatalog() {
    }

    public static boolean isMusic(String code) {
        return MUSIC_MAP.containsKey(code);
    }

    public static boolean isFlower(String code) {
        return FLOWER_MAP.containsKey(code);
    }
}
