# 扔东西追悼会 / trash-funeral

Entertainment-only web app: photograph an object you are throwing away, hold a short humorous funeral, edit the eulogy / music / flowers, consult a parody Chinese almanac (黄历) for *where to toss it*, and keep a private cemetery of past rites.

**本应用只供娱乐。它不是市政回收、垃圾分类或环保投放指引。真实处理请遵循当地规定。**  
**Entertainment only. Not municipal recycling, waste sorting, or environmental guidance.**

MIT licensed. Chinese + English UI.

## Product flow

1. Register / sign in (JWT). Seed user: `demo` / `demo123`.
2. Upload a photo.
3. Identify the object: **OpenAI Vision** when `OPENAI_API_KEY` is set; otherwise a **deterministic mock**.
4. Load a funeral script for the object type (electronics, clothes, food, paper, toy, plant, cosmetics, furniture, packaging, other).
5. Edit eulogy, music, and flowers.
6. Read a humorous 黄历 (宜/忌/方位/吉时). Joke directions only.
7. Publish a funeral card (public token URL) and browse personal cemetery history.

## Architecture

```
backend/          Spring Boot 3.4, Java 17, layered MVC
  common/       API envelope, errors
  auth/         users, JWT, SecurityFilterChain
  funeral/      domain, Flyway, OpenAPI, vision, almanac, bootable jar
frontend/       React 19 + Vite + TypeScript SPA (responsive)
docs/schema.md  PK/FK documentation
```

- MySQL 8 in Docker / production; `local` and `test` profiles use H2 (Flyway off, Hibernate DDL).
- OpenAPI UI: `http://localhost:8080/swagger-ui.html` (`/v3/api-docs`).
- Photos stored under `UPLOAD_DIR` (default `./uploads`), not in the database.

## Quick start (Docker Compose)

```bash
cp .env.example .env
docker compose up --build
```

- App: http://localhost/
- API: http://localhost:8080
- OpenAPI: http://localhost:8080/swagger-ui.html

Optional: set `OPENAI_API_KEY` in `.env` for real identification.

## Local development (no Docker)

Backend (H2 file DB, no MySQL required):

```bash
cd backend
mvn -pl funeral -am spring-boot:run -Dspring-boot.run.profiles=local
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Vite proxies `/api` to `http://127.0.0.1:8080`. Open http://localhost:5173.

Against MySQL 8 instead of H2, omit the `local` profile and use the variables in `.env.example`.

## Tests / smoke

```bash
cd backend && mvn test          # JUnit smoke: auth + funeral flow + OpenAPI
chmod +x scripts/smoke.sh
./scripts/smoke.sh              # HTTP smoke vs a running server
cd frontend && npm run build
```

## Configuration

See `.env.example`. Important keys:

| Variable | Meaning |
| --- | --- |
| `SPRING_DATASOURCE_*` | MySQL JDBC |
| `JWT_SECRET` | HMAC secret (≥32 chars in real deploys) |
| `OPENAI_API_KEY` | Optional; mock identifier if empty |
| `OPENAI_MODEL` | Default `gpt-4o-mini` |
| `UPLOAD_DIR` | Photo directory |

## License

[MIT](LICENSE)
