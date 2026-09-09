export type ApiOk<T> = { ok: true; data: T; message: null };
export type ApiFail = { ok: false; data: null; message: string };

const TOKEN_KEY = "tf.token";

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

async function parse<T>(res: Response): Promise<T> {
  const text = await res.text();
  const json = text ? (JSON.parse(text) as ApiOk<T> | ApiFail) : null;
  if (!res.ok || !json || json.ok === false) {
    const msg = json && "message" in json && json.message ? json.message : `HTTP ${res.status}`;
    throw new Error(msg);
  }
  return json.data;
}

export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  const token = getToken();
  if (token && !headers.has("Authorization")) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  if (init.body && !(init.body instanceof FormData) && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }
  const res = await fetch(path, { ...init, headers });
  if (res.status === 401) {
    setToken(null);
  }
  return parse<T>(res);
}

export type User = { id: number; username: string; email: string; displayName: string };
export type TokenResponse = { token: string; tokenType: string; user: User };
export type CatalogItem = { code: string; nameZh: string; nameEn: string };
export type ObjectType = CatalogItem & { defaultMusic: string; defaultFlowers: string };
export type Catalog = { objectTypes: ObjectType[]; music: CatalogItem[]; flowers: CatalogItem[] };
export type Almanac = {
  date: string;
  objectTypeCode: string;
  stemBranchZh: string;
  stemBranchEn: string;
  directionZh: string;
  directionEn: string;
  whereToThrowZh: string;
  whereToThrowEn: string;
  suitableZh: string[];
  suitableEn: string[];
  avoidZh: string[];
  avoidEn: string[];
  luckyHourZh: string;
  luckyHourEn: string;
  verseZh: string;
  verseEn: string;
  disclaimerZh: string;
  disclaimerEn: string;
};
export type Identification = {
  photoId: string;
  label: string;
  objectTypeCode: string;
  objectTypeNameZh: string;
  objectTypeNameEn: string;
  confidence: number;
  mock: boolean;
  suggestedEulogyZh: string;
  suggestedEulogyEn: string;
  suggestedMusic: string;
  suggestedFlowers: string;
};
export type Funeral = {
  id: number;
  photoId: string;
  photoUrl: string;
  identifiedLabel: string;
  objectTypeCode: string;
  objectTypeNameZh: string;
  objectTypeNameEn: string;
  objectName: string;
  eulogy: string;
  musicCode: string;
  flowersCode: string;
  locale: string;
  almanac: Almanac;
  ritualDate: string;
  publicToken: string;
  publicUrl: string;
  status: "DRAFT" | "COMPLETED";
  createdAt: string;
  updatedAt: string;
};
export type FuneralSummary = {
  id: number;
  photoUrl: string;
  objectName: string;
  objectTypeCode: string;
  objectTypeNameZh: string;
  objectTypeNameEn: string;
  status: string;
  ritualDate: string;
  createdAt: string;
};
export type PublicCard = {
  objectName: string;
  identifiedLabel: string;
  objectTypeNameZh: string;
  objectTypeNameEn: string;
  eulogy: string;
  musicCode: string;
  flowersCode: string;
  photoUrl: string;
  almanac: Almanac;
  locale: string;
  ritualDate: string;
};
