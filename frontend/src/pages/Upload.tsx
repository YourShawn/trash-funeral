import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api, type Identification } from "../api/client";
import Disclaimer from "../components/Disclaimer";
import { useAuth } from "../auth/AuthContext";

export default function Upload() {
  const { t, locale } = useAuth();
  const navigate = useNavigate();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [preview, setPreview] = useState<string | null>(null);

  async function onFile(file: File) {
    setError(null);
    setPreview(URL.createObjectURL(file));
    setBusy(true);
    try {
      const form = new FormData();
      form.append("photo", file);
      const identified = await api<Identification>("/api/funerals/identify", { method: "POST", body: form });
      sessionStorage.setItem("tf.identify", JSON.stringify(identified));
      navigate("/ceremony/new", { state: { identified } });
    } catch (err) {
      setError(err instanceof Error ? err.message : t.error);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="panel wide">
      <h1>{t.uploadTitle}</h1>
      <p className="hint">{t.uploadHint}</p>
      {error && <p className="error">{error}</p>}
      <label className="drop">
        <input
          type="file"
          accept="image/jpeg,image/png,image/webp,image/gif"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) void onFile(file);
          }}
        />
        {preview ? <img src={preview} alt="" /> : <span>{locale === "zh" ? "点击或拍照上传" : "Tap or photograph"}</span>}
      </label>
      {busy && <p className="hint">{t.identifying}</p>}
      <Disclaimer />
    </div>
  );
}
