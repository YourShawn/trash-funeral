import { FormEvent, useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { api, type Catalog, type CatalogItem, type Funeral, type Identification } from "../api/client";
import AlmanacCard from "../components/AlmanacCard";
import { useAuth } from "../auth/AuthContext";

function labelOf(items: CatalogItem[], code: string, locale: string) {
  const item = items.find((i) => i.code === code);
  if (!item) return code;
  return locale === "zh" ? item.nameZh : item.nameEn;
}

export default function Ceremony() {
  const { t, locale } = useAuth();
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const identifiedFromNav = (location.state as { identified?: Identification } | null)?.identified;
  const [catalog, setCatalog] = useState<Catalog | null>(null);
  const [identified, setIdentified] = useState<Identification | null>(identifiedFromNav ?? null);
  const [funeral, setFuneral] = useState<Funeral | null>(null);
  const [objectName, setObjectName] = useState("");
  const [eulogy, setEulogy] = useState("");
  const [musicCode, setMusicCode] = useState("");
  const [flowersCode, setFlowersCode] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const isNew = id === "new" || !id;

  useEffect(() => {
    void api<Catalog>("/api/catalog").then(setCatalog);
  }, []);

  useEffect(() => {
    if (isNew) {
      const stored = identifiedFromNav ?? (sessionStorage.getItem("tf.identify") ? (JSON.parse(sessionStorage.getItem("tf.identify")!) as Identification) : null);
      if (!stored) {
        navigate("/new");
        return;
      }
      setIdentified(stored);
      setObjectName(stored.label);
      setEulogy(locale === "en" ? stored.suggestedEulogyEn : stored.suggestedEulogyZh);
      setMusicCode(stored.suggestedMusic);
      setFlowersCode(stored.suggestedFlowers);
      return;
    }
    void api<Funeral>(`/api/funerals/${id}`)
      .then((f) => {
        setFuneral(f);
        setObjectName(f.objectName);
        setEulogy(f.eulogy);
        setMusicCode(f.musicCode);
        setFlowersCode(f.flowersCode);
      })
      .catch((err: Error) => setError(err.message));
  }, [id, isNew, identifiedFromNav, locale, navigate]);

  const photoSrc = useMemo(() => {
    if (funeral) return funeral.photoUrl;
    return null;
  }, [funeral]);

  async function onSave(e: FormEvent) {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      if (isNew && identified) {
        const created = await api<Funeral>("/api/funerals", {
          method: "POST",
          body: JSON.stringify({
            photoId: identified.photoId,
            objectTypeCode: identified.objectTypeCode,
            objectName,
            identifiedLabel: identified.label,
            eulogy,
            musicCode,
            flowersCode,
            locale,
          }),
        });
        sessionStorage.removeItem("tf.identify");
        navigate(`/ceremony/${created.id}`);
        setFuneral(created);
        return;
      }
      if (funeral) {
        const updated = await api<Funeral>(`/api/funerals/${funeral.id}`, {
          method: "PUT",
          body: JSON.stringify({ objectName, eulogy, musicCode, flowersCode, locale }),
        });
        setFuneral(updated);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : t.error);
    } finally {
      setBusy(false);
    }
  }

  async function onComplete() {
    if (!funeral) return;
    setBusy(true);
    try {
      const updated = await api<Funeral>(`/api/funerals/${funeral.id}/complete`, { method: "POST" });
      setFuneral(updated);
      navigate(`/card/${updated.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : t.error);
    } finally {
      setBusy(false);
    }
  }

  if (!catalog || (isNew && !identified) || (!isNew && !funeral && !error)) {
    return <p className="hint">{t.identifying}</p>;
  }

  return (
    <form className="ceremony" onSubmit={onSave}>
      <div>
        {photoSrc && <img className="portrait" src={photoSrc} alt="" />}
        {identified && (
          <p className="hint">
            {identified.mock ? t.mockNote : t.aiNote} · {Math.round(identified.confidence * 100)}%
          </p>
        )}
        {funeral && <AlmanacCard almanac={funeral.almanac} />}
      </div>
      <div className="panel">
        <h1>{t.identified}</h1>
        {error && <p className="error">{error}</p>}
        <p className="meta">
          {t.type}: {locale === "zh" ? identified?.objectTypeNameZh ?? funeral?.objectTypeNameZh : identified?.objectTypeNameEn ?? funeral?.objectTypeNameEn}
        </p>
        <label>
          {t.objectName}
          <input value={objectName} onChange={(e) => setObjectName(e.target.value)} required />
        </label>
        <label>
          {t.eulogy}
          <textarea rows={6} value={eulogy} onChange={(e) => setEulogy(e.target.value)} required />
        </label>
        <label>
          {t.music}
          <select value={musicCode} onChange={(e) => setMusicCode(e.target.value)}>
            {catalog.music.map((m) => (
              <option key={m.code} value={m.code}>
                {labelOf(catalog.music, m.code, locale)}
              </option>
            ))}
          </select>
        </label>
        <label>
          {t.flowers}
          <select value={flowersCode} onChange={(e) => setFlowersCode(e.target.value)}>
            {catalog.flowers.map((f) => (
              <option key={f.code} value={f.code}>
                {labelOf(catalog.flowers, f.code, locale)}
              </option>
            ))}
          </select>
        </label>
        <div className="actions">
          <button className="btn" disabled={busy} type="submit">
            {t.saveCeremony}
          </button>
          {funeral && (
            <button className="btn ghost" type="button" disabled={busy} onClick={() => void onComplete()}>
              {t.complete}
            </button>
          )}
        </div>
      </div>
    </form>
  );
}
