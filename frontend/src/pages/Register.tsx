import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function Register() {
  const { t, register } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setBusy(true);
    setError(null);
    try {
      await register({ username, email, password, displayName: displayName || undefined });
      navigate("/new");
    } catch (err) {
      setError(err instanceof Error ? err.message : t.error);
    } finally {
      setBusy(false);
    }
  }

  return (
    <form className="panel" onSubmit={onSubmit}>
      <h1>{t.register}</h1>
      {error && <p className="error">{error}</p>}
      <label>
        {t.username}
        <input value={username} onChange={(e) => setUsername(e.target.value)} minLength={3} required />
      </label>
      <label>
        {t.email}
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
      </label>
      <label>
        {t.password}
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} minLength={6} required />
      </label>
      <label>
        {t.displayName}
        <input value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
      </label>
      <button className="btn" disabled={busy} type="submit">
        {t.register}
      </button>
      <Link to="/login">{t.haveAccount}</Link>
    </form>
  );
}
