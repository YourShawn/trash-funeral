import type { Almanac } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function AlmanacCard({ almanac }: { almanac: Almanac }) {
  const { locale, t } = useAuth();
  const zh = locale === "zh";
  return (
    <section className="almanac">
      <h3>{t.almanac}</h3>
      <p className="stem">{zh ? almanac.stemBranchZh : almanac.stemBranchEn}</p>
      <dl>
        <div>
          <dt>{t.direction}</dt>
          <dd>{zh ? almanac.whereToThrowZh : almanac.whereToThrowEn}</dd>
        </div>
        <div>
          <dt>{t.luckyHour}</dt>
          <dd>{zh ? almanac.luckyHourZh : almanac.luckyHourEn}</dd>
        </div>
        <div>
          <dt>{t.suitable}</dt>
          <dd>{(zh ? almanac.suitableZh : almanac.suitableEn).join(" · ")}</dd>
        </div>
        <div>
          <dt>{t.avoid}</dt>
          <dd>{(zh ? almanac.avoidZh : almanac.avoidEn).join(" · ")}</dd>
        </div>
        <div>
          <dt>{t.verse}</dt>
          <dd className="verse">{zh ? almanac.verseZh : almanac.verseEn}</dd>
        </div>
      </dl>
      <p className="fine">{zh ? almanac.disclaimerZh : almanac.disclaimerEn}</p>
    </section>
  );
}
