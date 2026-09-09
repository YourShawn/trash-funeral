import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api, type Catalog, type Funeral } from "../api/client";
import AlmanacCard from "../components/AlmanacCard";
import { useAuth } from "../auth/AuthContext";

export default function CardPage() {
  const { t, locale } = useAuth();
  const { id } = useParams();
  const [funeral, setFuneral] = useState<Funeral | null>(null);
  const [catalog, setCatalog] = useState<Catalog | null>(null);
  const [copied, setCopied] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void api<Catalog>("/api/catalog").then(setCatalog);
    void api<Funeral>(`/api/funerals/${id}`)
      .then(setFuneral)
      .catch((err: Error) => setError(err.message));
  }, [id]);

  if (error) return <p className="error">{error}</p>;
  if (!funeral || !catalog) return <p className="hint">…</p>;

  const music = catalog.music.find((m) => m.code === funeral.musicCode);
  const flowers = catalog.flowers.find((f) => f.code === funeral.flowersCode);
  const publicLink = `${window.location.origin}/c/${funeral.publicToken}`;

  return (
    <div className="card-page">
      <article className="memorial">
        <p className="eyebrow">{t.card}</p>
        <img src={funeral.photoUrl} alt="" />
        <h1>{funeral.objectName}</h1>
        <p className="meta">
          {locale === "zh" ? funeral.objectTypeNameZh : funeral.objectTypeNameEn} · {t.ritualDate} {funeral.ritualDate}
        </p>
        <blockquote>{funeral.eulogy}</blockquote>
        <p>
          {t.music}: {locale === "zh" ? music?.nameZh : music?.nameEn}
        </p>
        <p>
          {t.flowers}: {locale === "zh" ? flowers?.nameZh : flowers?.nameEn}
        </p>
      </article>
      <AlmanacCard almanac={funeral.almanac} />
      <div className="actions">
        <button
          className="btn"
          type="button"
          onClick={async () => {
            await navigator.clipboard.writeText(publicLink);
            setCopied(true);
          }}
        >
          {copied ? t.copied : t.share}
        </button>
        <Link className="btn ghost" to="/cemetery">
          {t.cemetery}
        </Link>
      </div>
    </div>
  );
}
