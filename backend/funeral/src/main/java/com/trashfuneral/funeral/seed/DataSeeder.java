package com.trashfuneral.funeral.seed;

import com.trashfuneral.auth.domain.User;
import com.trashfuneral.auth.repo.UserRepository;
import com.trashfuneral.funeral.domain.ObjectType;
import com.trashfuneral.funeral.repo.ObjectTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ObjectTypeRepository objectTypes;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ObjectTypeRepository objectTypes, UserRepository users, PasswordEncoder passwordEncoder) {
        this.objectTypes = objectTypes;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedTypes();
        seedDemoUser();
    }

    private void seedDemoUser() {
        if (users.existsByUsername("demo")) {
            return;
        }
        User user = new User();
        user.setUsername("demo");
        user.setEmail("demo@trashfuneral.local");
        user.setPasswordHash(passwordEncoder.encode("demo123"));
        user.setDisplayName("Demo Mourner");
        users.save(user);
        log.info("Seeded demo user demo / demo123");
    }

    private void seedTypes() {
        upsert("ELECTRONICS", "电子遗物", "E-waste spirit",
                "你曾传递电流与消息，直到接口松了、电池鼓了、世界换了接口。此刻我们拔掉最后一根线，愿你在回收宇宙里电压永平。",
                "You carried current and messages until the port loosened and the world changed plugs. We unplug you now. May your voltage rest.",
                "silent_beep", "incense_usb",
                "玩笑方位：书桌东南。真实处理请走当地电子回收，而非本黄历。",
                "Joke direction: desk southeast. Real e-waste belongs in local electronics recycling, not this almanac.");
        upsert("CLOTHES", "衣冠冢", "Wardrobe ghost",
                "你陪我走过季节与场合，领口渐渐认不出自己。叠好，道谢，去吧——衣橱需要呼吸。",
                "You walked seasons and occasions with me until the collar forgot itself. Folded, thanked, released. Closets need air.",
                "moon_expired", "white_mum",
                "玩笑方位：阳台正北最后一次晾晒。旧衣去向请遵循本地捐赠/纺织回收。",
                "Joke direction: balcony due north. Follow local donation or textile recycling for real clothes.");
        upsert("FOOD", "过期祭品", "Expired offering",
                "保质期是缘分的倒计时。你曾香过、甜过、冷过。我们不再打开，只鞠躬，然后让冰箱恢复宁静。",
                "Expiry dates are countdown clocks for fate. You were fragrant, then sweet, then cold. We will not open you. Bow, and let the fridge go quiet.",
                "farewell_bin", "onion_tears",
                "玩笑方位：厨房西南厨余祭坛。真正的食物残渣请按当地厨余/垃圾规则处理。",
                "Joke direction: kitchen southwest. Follow local food-waste rules for the real thing.");
        upsert("PAPER", "纸灰", "Paper ashes",
                "说明书、小票、便签——你们记录过瞬间，也制造过抽屉的雪崩。今日火是比喻，纸是告别。",
                "Manuals, receipts, sticky notes: you recorded moments and avalanched drawers. Today's fire is a metaphor. The paper is a goodbye.",
                "receipt_choir", "sticky_wreath",
                "玩笑方位：书房正东。废纸请走本地纸类回收，不要真的烧。",
                "Joke direction: study due east. Recycle paper locally; do not actually burn it.");
        upsert("TOY", "玩具英灵", "Toy valhalla",
                "发条停了，眼睛掉了一只，但你仍记得某年某日的笑声。英灵殿不收灰尘，只收故事。",
                "The spring wound down and one eye left, yet you still remember a year of laughter. Valhalla takes stories, not dust.",
                "bubble_wrap", "plastic_forever",
                "玩笑方位：床底西北。完好玩具可捐赠；本黄历不是弃置许可。",
                "Joke direction: under-bed northwest. Donate toys that still work; this is not a dumping permit.");
        upsert("PLANT", "枯荣", "Wilted companion",
                "浇也枯，不浇也枯。你教会我：有些绿意不属于这扇窗。归还阳光，土归土。",
                "Watered, you wilted. Ignored, you wilted. Some green was never meant for this window. Return the sunlight; earth to earth.",
                "canon_can", "white_mum",
                "玩笑方位：窗台正南。植物残体请按当地园艺垃圾规则，勿随便外抛。",
                "Joke direction: windowsill due south. Follow local garden-waste rules.");
        upsert("COSMETICS", "空瓶仙", "Empty-bottle immortal",
                "你承诺过光泽与勇气，直到刮板见光。空瓶成仙，镜子还在。我们卸下期待，留下干净的台面。",
                "You promised glow and courage until the spatula hit daylight. The bottle becomes immortal; the mirror stays. We leave a clean counter.",
                "chopin_trash", "barcode_lily",
                "玩笑方位：洗手台正西。空瓶是否可回收请看当地包装规则。",
                "Joke direction: sink due west. Check local packaging recycling for empty bottles.");
        upsert("FURNITURE", "大家具", "Bulky farewell",
                "你承担过重量与午后的懒。螺丝松了，故事还在。大件告别需要预约，不需要内疚。",
                "You held weight and lazy afternoons. The screws loosened; the stories stayed. Bulky farewells need appointments, not guilt.",
                "farewell_bin", "white_mum",
                "玩笑方位：门口东北。大件请预约当地清运，切勿随意丢在路边。",
                "Joke direction: doorway northeast. Book a real bulky-waste pickup; do not dump on the curb.");
        upsert("PACKAGING", "快递皮囊", "Parcel husk",
                "你的使命在开箱那一秒完成。泡沫、纸箱、胶带——谢谢护送。皮囊可去，期待留下。",
                "Your mission ended at the unboxing. Foam, carton, tape — thank you for the escort. The husk may leave; the anticipation stays.",
                "bubble_wrap", "sticky_wreath",
                "玩笑方位：玄关正西。纸箱请压扁回收，泡沫看当地规定。",
                "Joke direction: entryway due west. Flatten cartons for recycling; check local rules for foam.");
        upsert("OTHER", "无名之物", "Nameless object",
                "抽屉居民，来历不明，功用成谜。我们仍为你举行仪式：被看见，被命名，被放下。",
                "Drawer resident, origin unknown, purpose a riddle. You still get a rite: seen, named, set down.",
                "canon_can", "plastic_forever",
                "玩笑方位：房间中央。真实投放请先辨认材质并遵循本地分类。",
                "Joke direction: center of the room. Identify the material and follow local sorting.");
    }

    private void upsert(
            String code, String nameZh, String nameEn,
            String eulogyZh, String eulogyEn,
            String music, String flowers,
            String hintZh, String hintEn
    ) {
        ObjectType type = objectTypes.findByCode(code).orElseGet(ObjectType::new);
        type.setCode(code);
        type.setNameZh(nameZh);
        type.setNameEn(nameEn);
        type.setEulogyZh(eulogyZh);
        type.setEulogyEn(eulogyEn);
        type.setDefaultMusic(music);
        type.setDefaultFlowers(flowers);
        type.setThrowHintZh(hintZh);
        type.setThrowHintEn(hintEn);
        objectTypes.save(type);
    }
}
