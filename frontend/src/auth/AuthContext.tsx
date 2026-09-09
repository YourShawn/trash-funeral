import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { api, getToken, setToken, type TokenResponse, type User } from "../api/client";
import { strings, type Locale, type Strings } from "../i18n/strings";

type AuthState = {
  user: User | null;
  locale: Locale;
  t: Strings;
  login: (username: string, password: string) => Promise<void>;
  register: (payload: { username: string; email: string; password: string; displayName?: string }) => Promise<void>;
  logout: () => void;
  setLocale: (locale: Locale) => void;
};

const Ctx = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [locale, setLocaleState] = useState<Locale>(() => (localStorage.getItem("tf.locale") === "en" ? "en" : "zh"));

  useEffect(() => {
    if (!getToken()) return;
    api<User>("/api/auth/me")
      .then(setUser)
      .catch(() => {
        setToken(null);
        setUser(null);
      });
  }, []);

  const value = useMemo<AuthState>(() => {
    const applyAuth = (res: TokenResponse) => {
      setToken(res.token);
      setUser(res.user);
    };
    return {
      user,
      locale,
      t: strings[locale],
      login: async (username, password) => {
        applyAuth(await api<TokenResponse>("/api/auth/login", { method: "POST", body: JSON.stringify({ username, password }) }));
      },
      register: async (payload) => {
        applyAuth(await api<TokenResponse>("/api/auth/register", { method: "POST", body: JSON.stringify(payload) }));
      },
      logout: () => {
        setToken(null);
        setUser(null);
      },
      setLocale: (next) => {
        localStorage.setItem("tf.locale", next);
        setLocaleState(next);
      },
    };
  }, [user, locale]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useAuth() {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("AuthProvider missing");
  return ctx;
}
