import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function Layout() {
  const { user, t, locale, setLocale, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="shell">
      <header className="top">
        <Link to="/" className="brand">
          <span className="brand-mark">🕯</span>
          <span>
            <strong>{t.brand}</strong>
            <small>{t.brandEn}</small>
          </span>
        </Link>
        <nav>
          {user && (
            <>
              <NavLink to="/new">{t.start}</NavLink>
              <NavLink to="/cemetery">{t.cemetery}</NavLink>
            </>
          )}
          <button className="lang" type="button" onClick={() => setLocale(locale === "zh" ? "en" : "zh")}>
            {locale === "zh" ? "EN" : "中"}
          </button>
          {user ? (
            <button
              className="ghost"
              type="button"
              onClick={() => {
                logout();
                navigate("/");
              }}
            >
              {t.logout}
            </button>
          ) : (
            <NavLink to="/login">{t.login}</NavLink>
          )}
        </nav>
      </header>
      <main>
        <Outlet />
      </main>
      <footer>
        <p>
          MIT · {t.disclaimerTitle}: {t.disclaimer}
        </p>
      </footer>
    </div>
  );
}
