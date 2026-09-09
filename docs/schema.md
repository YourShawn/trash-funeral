# Database schema — trash-funeral

MySQL 8, utf8mb4. Migrations live in `backend/funeral/src/main/resources/db/migration/`.

```
users 1──< funerals >──1 object_types
```

## `users`

| Column | Type | Notes |
| --- | --- | --- |
| `id` | BIGINT PK AI | |
| `username` | VARCHAR(64) UNIQUE NOT NULL | login name |
| `email` | VARCHAR(191) UNIQUE NOT NULL | |
| `password_hash` | VARCHAR(100) NOT NULL | BCrypt |
| `display_name` | VARCHAR(80) NOT NULL | |
| `created_at` | DATETIME(6) NOT NULL | |
| `updated_at` | DATETIME(6) NOT NULL | |

## `object_types`

Catalog of funeral scripts (seeded). Codes: `ELECTRONICS`, `CLOTHES`, `FOOD`, `PAPER`, `TOY`, `PLANT`, `COSMETICS`, `FURNITURE`, `PACKAGING`, `OTHER`.

| Column | Type | Notes |
| --- | --- | --- |
| `id` | BIGINT PK AI | |
| `code` | VARCHAR(32) UNIQUE NOT NULL | |
| `name_zh` / `name_en` | VARCHAR(80) NOT NULL | |
| `eulogy_zh` / `eulogy_en` | TEXT NOT NULL | default eulogy |
| `default_music` | VARCHAR(64) NOT NULL | catalog code |
| `default_flowers` | VARCHAR(64) NOT NULL | catalog code |
| `throw_hint_zh` / `throw_hint_en` | TEXT NOT NULL | entertainment hint only |

## `funerals`

| Column | Type | Notes |
| --- | --- | --- |
| `id` | BIGINT PK AI | |
| `user_id` | BIGINT NOT NULL | FK → `users.id` ON DELETE CASCADE |
| `object_type_id` | BIGINT NOT NULL | FK → `object_types.id` |
| `photo_id` | VARCHAR(48) NOT NULL | stored filename (UUID + extension) |
| `identified_label` | VARCHAR(160) NOT NULL | AI or mock label |
| `object_name` | VARCHAR(120) NOT NULL | editable |
| `eulogy` | TEXT NOT NULL | editable |
| `music_code` | VARCHAR(64) NOT NULL | |
| `flowers_code` | VARCHAR(64) NOT NULL | |
| `locale` | VARCHAR(8) NOT NULL | `zh` or `en` |
| `almanac_json` | TEXT NOT NULL | snapshot of parody 黄历 |
| `ritual_date` | DATE NOT NULL | |
| `public_token` | VARCHAR(36) UNIQUE NOT NULL | public card URL token |
| `status` | VARCHAR(16) NOT NULL | `DRAFT` or `COMPLETED` |
| `created_at` / `updated_at` | DATETIME(6) NOT NULL | |

Index: `(user_id, created_at)` for cemetery history.

Public cards are only served when `status = COMPLETED`. Photos are stored on disk (`UPLOAD_DIR`), not in MySQL.

Demo login is **not** inserted by Flyway; `DataSeeder` creates user `demo` / `demo123` on first boot if missing.
