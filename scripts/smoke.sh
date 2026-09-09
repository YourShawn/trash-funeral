#!/usr/bin/env bash
# Live HTTP smoke against a running backend (default http://127.0.0.1:8080).
set -euo pipefail
BASE="${1:-http://127.0.0.1:8080}"

echo "== health =="
curl -fsS "$BASE/actuator/health" | grep -q UP

echo "== catalog =="
curl -fsS "$BASE/api/catalog" | grep -q objectTypes

echo "== almanac =="
curl -fsS "$BASE/api/almanac?objectType=FOOD" | grep -q disclaimer

echo "== login demo =="
TOKEN=$(curl -fsS -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"demo123"}' \
  "$BASE/api/auth/login" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

echo "== identify =="
PNG=$(mktemp /tmp/tfXXXX.png)
python3 - << 'PY' "$PNG"
import sys, base64, pathlib
png = base64.b64decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==")
pathlib.Path(sys.argv[1]).write_bytes(png)
PY
IDENT=$(curl -fsS -H "Authorization: Bearer $TOKEN" -F "photo=@${PNG};type=image/png" "$BASE/api/funerals/identify")
python3 - << 'PY' "$IDENT"
import json,sys
d=json.loads(sys.argv[1])["data"]
assert d["photoId"] and d["objectTypeCode"] and d["suggestedEulogyZh"]
print("identified", d["objectTypeCode"], "mock=", d["mock"])
open("/tmp/tf-ident.json","w").write(json.dumps(d))
PY

echo "== create + complete =="
python3 - << PY
import json, urllib.request, os
base = "$BASE"
token = "$TOKEN"
d = json.load(open("/tmp/tf-ident.json"))
body = json.dumps({
  "photoId": d["photoId"],
  "objectTypeCode": d["objectTypeCode"],
  "objectName": "smoke relic",
  "identifiedLabel": d["label"],
  "eulogy": d["suggestedEulogyEn"],
  "musicCode": d["suggestedMusic"],
  "flowersCode": d["suggestedFlowers"],
  "locale": "en",
}).encode()
req = urllib.request.Request(base + "/api/funerals", data=body, headers={
  "Authorization": "Bearer " + token,
  "Content-Type": "application/json",
})
created = json.load(urllib.request.urlopen(req))["data"]
fid = created["id"]
req2 = urllib.request.Request(base + f"/api/funerals/{fid}/complete", data=b"", method="POST", headers={
  "Authorization": "Bearer " + token,
})
done = json.load(urllib.request.urlopen(req2))["data"]
assert done["status"] == "COMPLETED"
card = json.load(urllib.request.urlopen(base + "/api/public/cards/" + done["publicToken"]))["data"]
assert card["objectName"] == "smoke relic"
print("funeral", fid, "card ok")
PY

echo "SMOKE OK"
