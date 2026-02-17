## Troubleshooting

### 1) `500` on register after architecture changes
Cause: old table schema conflicts.
Fix:
```bash
psql -h localhost -U postgres -d hyrup -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
```
Then restart app.

### 2) `echo -n "$JWT_SECRET" | wc -c` gives `0`
Cause: `.env` not loaded in current terminal.
Fix:
```bash
set -a
source .env
set +a
```

### 3) Protected requests return `401`
- Run login first
- Confirm `token` variable is set in Postman
- Ensure header is `Authorization: Bearer <token>`