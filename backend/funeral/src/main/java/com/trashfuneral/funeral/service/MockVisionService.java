package com.trashfuneral.funeral.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.CRC32;

@Component
public class MockVisionService {

    private static final List<String[]> SAMPLES = List.of(
            new String[]{"ELECTRONICS", "一根寿终正寝的充电线", "A charging cable that gave its last volt"},
            new String[]{"ELECTRONICS", "老年机时代的旧手机", "A phone from a previous geological era"},
            new String[]{"CLOTHES", "一双开口笑的袜子", "A sock that finally smiled at the toe"},
            new String[]{"CLOTHES", "洗到透明的 T 恤", "A T-shirt washed into translucence"},
            new String[]{"FOOD", "过期三天仍自信的酸奶", "Yogurt three days past its bravado"},
            new String[]{"FOOD", "外卖盒里的半份遗憾", "Half a regret in a takeout box"},
            new String[]{"PAPER", "再也对不上的说明书", "A manual that no longer matches anything"},
            new String[]{"PAPER", "皱成命运的购物小票", "A receipt crumpled by destiny"},
            new String[]{"TOY", "缺了一只眼的玩偶", "A plush toy missing one eye of wisdom"},
            new String[]{"PLANT", "浇也枯、不浇也枯的绿萝", "A pothos that wilted either way"},
            new String[]{"COSMETICS", "刮到见光的空瓶", "A bottle scraped until it saw daylight"},
            new String[]{"FURNITURE", "一条腿已和解的凳子", "A stool that made peace with three legs"},
            new String[]{"PACKAGING", "拆完就成历史的纸箱", "A carton whose purpose ended at unboxing"},
            new String[]{"OTHER", "说不清来历的抽屉居民", "A drawer resident of unknown origin"}
    );

    public VisionResult identify(byte[] imageBytes, String filename) {
        CRC32 crc = new CRC32();
        crc.update(imageBytes == null ? new byte[0] : imageBytes);
        if (filename != null) {
            crc.update(filename.getBytes(StandardCharsets.UTF_8));
        }
        int idx = Math.floorMod((int) crc.getValue(), SAMPLES.size());
        String[] sample = SAMPLES.get(idx);
        return new VisionResult(sample[1] + " / " + sample[2], sample[0], 0.62 + (idx % 5) * 0.05, true);
    }
}
