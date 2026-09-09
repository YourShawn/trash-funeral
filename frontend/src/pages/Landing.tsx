import { Link } from "react-router-dom";
import Disclaimer from "../components/Disclaimer";
import { useAuth } from "../auth/AuthContext";

export default function Landing() {
  const { t, user } = useAuth();
  return (
    <div className="hero">
      <div className="incense" aria-hidden="true" />
      <p className="eyebrow">{t.landingCta}</p>
      <h1>{t.brand}</h1>
      <p className="lede">{t.tagline}</p>
      <div className="actions">
        <Link className="btn" to={user ? "/new" : "/login"}>
          {t.start}
        </Link>
        {user && (
          <Link className="btn ghost" to="/cemetery">
            {t.cemetery}
          </Link>
        )}
      </div>
      <Disclaimer />
    </div>
  );
}
