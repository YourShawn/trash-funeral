import { useAuth } from "../auth/AuthContext";

export default function Disclaimer() {
  const { t } = useAuth();
  return (
    <aside className="disclaimer">
      <strong>{t.disclaimerTitle}</strong>
      <p>{t.disclaimer}</p>
    </aside>
  );
}
