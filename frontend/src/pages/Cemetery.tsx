import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api, type FuneralSummary } from "../api/client";
import { useAuth } from "../auth/AuthContext";

export default function Cemetery() {
  const { t, locale } = useAuth();
  const [items, setItems] = useState<FuneralSummary[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void api<FuneralSummary[]>("/api/funerals")
      .then(setItems)
      .catch((err: Error) => setError(err.message));
  }, []);

  if (error) return <p className="error">{error}</p>;
  if (!items) return <p className="hint">…</p>;
  if (items.length === 0) {
    return (
      <div className="panel">
        <h1>{t.cemetery}</h1>
        <p>{t.emptyCemetery}</p>
        <Link className="btn" to="/new">
          {t.start}
        </Link>
      </div>
    );
  }

  return (
    <div>
      <h1>{t.cemetery}</h1>
      <ul className="graves">
        {items.map((item) => (
          <li key={item.id}>
            <Link to={item.status === "COMPLETED" ? `/card/${item.id}` : `/ceremony/${item.id}`}>
              <img src={item.photoUrl} alt="" />
              <div>
                <strong>{item.objectName}</strong>
                <span>{locale === "zh" ? item.objectTypeNameZh : item.objectTypeNameEn}</span>
                <span>
                  {item.ritualDate} · {item.status === "COMPLETED" ? t.completed : t.draft}
                </span>
              </div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
