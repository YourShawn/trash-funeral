import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { api, type Catalog, type PublicCard } from "../api/client";
import AlmanacCard from "../components/AlmanacCard";
import { useAuth } from "../auth/AuthContext";

export default function PublicCardPage() {
  const { t, locale } = useAuth();
  const { token } = useParams();
  const [card, setCard] = useState<PublicCard | null>(null);
  const [catalog, setCatalog] = useState<Catalog | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void api<Catalog>("/api/catalog").then(setCatalog);
    void api<PublicCard>(`/api/public/cards/${token}`)
      .then(setCard)
      .catch((err: Error) => setError(err.message));
  }, [token]);

  if (error) return <p className="error">{error}</p>;
  if (!card || !catalog) return <p className="hint">…</p>;
  const music = catalog.music.find((m) => m.code === card.musicCode);
  const flowers = catalog.flowers.find((f) => f.code === card.flowersCode);

  return (
    <article className="memorial public">
      <p className="eyebrow">{t.card}</p>
      <img src={card.photoUrl} alt="" />
      <h1>{card.objectName}</h1>
      <p className="meta">
        {locale === "zh" ? card.objectTypeNameZh : card.objectTypeNameEn} · {card.ritualDate}
      </p>
      <blockquote>{card.eulogy}</blockquote>
      <p>
        {t.music}: {locale === "zh" ? music?.nameZh : music?.nameEn}
      </p>
      <p>
        {t.flowers}: {locale === "zh" ? flowers?.nameZh : flowers?.nameEn}
      </p>
      <AlmanacCard almanac={card.almanac} />
    </article>
  );
}
