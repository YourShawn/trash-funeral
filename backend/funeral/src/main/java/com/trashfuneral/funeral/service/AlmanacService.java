package com.trashfuneral.funeral.service;

import com.trashfuneral.funeral.dto.AlmanacResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AlmanacService {

    private static final String[] STEMS_ZH = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] BRANCHES_ZH = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final String[] STEMS_EN = {"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"};
    private static final String[] BRANCHES_EN = {"Zi", "Chou", "Yin", "Mao", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"};
    private static final String[] DIRS_ZH = {"正东", "东南", "正南", "西南", "正西", "西北", "正北", "东北"};
    private static final String[] DIRS_EN = {"due east", "southeast", "due south", "southwest", "due west", "northwest", "due north", "northeast"};
    private static final String[] HOURS_ZH = {"辰时（7–9）", "巳时（9–11）", "午时（11–13）", "未时（13–15）", "申时（15–17）"};
    private static final String[] HOURS_EN = {"7–9 Chen hour", "9–11 Si hour", "11–13 Wu hour", "13–15 Wei hour", "15–17 Shen hour"};

    private static final String DISCLAIMER_ZH =
            "本黄历纯属娱乐，不是垃圾分类、市政回收或环保投放指引。真实投放请遵循当地规定。";
    private static final String DISCLAIMER_EN =
            "This almanac is entertainment only. It is not municipal recycling, waste-sorting, or environmental guidance. Follow local rules for real disposal.";

    public AlmanacResponse forType(String objectTypeCode, LocalDate date) {
        int seed = Math.abs((objectTypeCode + date).hashCode());
        int stem = seed % 10;
        int branch = (seed / 3) % 12;
        int dir = (seed / 7) % 8;
        int hour = (seed / 11) % HOURS_ZH.length;
        DirectionHints hints = hintsFor(objectTypeCode, dir);
        return new AlmanacResponse(
                date.toString(),
                objectTypeCode,
                STEMS_ZH[stem] + BRANCHES_ZH[branch] + " · 断舍日",
                STEMS_EN[stem] + "-" + BRANCHES_EN[branch] + " Declutter Day",
                DIRS_ZH[dir],
                DIRS_EN[dir],
                hints.whereZh(),
                hints.whereEn(),
                suitableZh(objectTypeCode, seed),
                suitableEn(objectTypeCode, seed),
                avoidZh(seed),
                avoidEn(seed),
                HOURS_ZH[hour],
                HOURS_EN[hour],
                verseZh(objectTypeCode, seed),
                verseEn(objectTypeCode, seed),
                DISCLAIMER_ZH,
                DISCLAIMER_EN
        );
    }

    private DirectionHints hintsFor(String code, int dir) {
        String dZh = DIRS_ZH[dir];
        String dEn = DIRS_EN[dir];
        return switch (code) {
            case "FOOD" -> new DirectionHints(
                    "厨房" + dZh + " · 厨余祭坛（玩笑方位，非分类建议）",
                    "Kitchen " + dEn + " · the leftover altar (joke direction, not sorting advice)"
            );
            case "ELECTRONICS" -> new DirectionHints(
                    "书桌" + dZh + " · 数据线成佛处（玩笑）",
                    "Desk " + dEn + " · where cables attain nirvana (joke)"
            );
            case "CLOTHES" -> new DirectionHints(
                    "阳台" + dZh + " · 最后一次晾晒（玩笑）",
                    "Balcony " + dEn + " · one last airing (joke)"
            );
            case "PLANT" -> new DirectionHints(
                    "窗台" + dZh + " · 归还阳光（玩笑）",
                    "Windowsill " + dEn + " · return the sunlight (joke)"
            );
            case "PACKAGING" -> new DirectionHints(
                    "玄关" + dZh + " · 快递皮囊谢幕（玩笑）",
                    "Entryway " + dEn + " · parcel husk curtain call (joke)"
            );
            default -> new DirectionHints(
                    "房间" + dZh + " · 今日抛物吉位（玩笑，非回收点）",
                    "Room " + dEn + " · today's auspicious toss (joke, not a recycling point)"
            );
        };
    }

    private List<String> suitableZh(String code, int seed) {
        List<String> common = List.of("默哀三秒", "轻拿轻放", "说一句谢谢你", "拍照留档后放手");
        String extra = switch (code) {
            case "FOOD" -> "对过期日期鞠躬";
            case "ELECTRONICS" -> "拔掉最后一根线";
            case "CLOTHES" -> "叠得整整齐齐再告别";
            default -> "给抽屉留出空位";
        };
        return List.of(common.get(seed % 4), extra, common.get((seed + 1) % 4));
    }

    private List<String> suitableEn(String code, int seed) {
        List<String> common = List.of("observe three seconds of silence", "set it down gently", "say thank you once", "photograph, then let go");
        String extra = switch (code) {
            case "FOOD" -> "bow to the expiry date";
            case "ELECTRONICS" -> "unplug the last cable";
            case "CLOTHES" -> "fold it neatly before goodbye";
            default -> "leave a vacancy in the drawer";
        };
        return List.of(common.get(seed % 4), extra, common.get((seed + 1) % 4));
    }

    private List<String> avoidZh(int seed) {
        List<String> all = List.of(
                "凌晨三点翻找",
                "回头看超过两次",
                "在朋友圈发「求收留」",
                "假装明天再扔",
                "睹物思情超过三分钟"
        );
        return List.of(all.get(seed % 5), all.get((seed + 2) % 5));
    }

    private List<String> avoidEn(int seed) {
        List<String> all = List.of(
                "rummaging at 3 a.m.",
                "looking back more than twice",
                "posting “anyone want this?”",
                "pretending you will toss it tomorrow",
                "nostalgia longer than three minutes"
        );
        return List.of(all.get(seed % 5), all.get((seed + 2) % 5));
    }

    private String verseZh(String code, int seed) {
        List<String> verses = List.of(
                "物来物去皆是客，抽屉不是养老院。",
                "今日一抛轻似羽，明日空间亮如许。",
                "你曾照亮我的桌角，此刻请去风里歇歇。",
                "保质期是缘分的倒计时，缘尽不怨。"
        );
        return verses.get(Math.abs((code + seed).hashCode()) % verses.size());
    }

    private String verseEn(String code, int seed) {
        List<String> verses = List.of(
                "Things arrive as guests; drawers are not nursing homes.",
                "Today it leaves lightly; tomorrow the shelf shines.",
                "You lit a corner of my desk. Rest now in the wind.",
                "Expiry dates are countdown clocks for fate. No hard feelings."
        );
        return verses.get(Math.abs((code + seed).hashCode()) % verses.size());
    }

    private record DirectionHints(String whereZh, String whereEn) {
    }
}
