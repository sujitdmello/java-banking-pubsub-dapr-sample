# Public API

This is the public API for starting money transfers.

## Run the app

```bash
./gradlew bootRun
```

## Create New Transfer

```bash
curl -X POST \
  http://localhost:8080/transfers \
  -H 'Content-Type: application/json' \
  -d '{
    "sender": "A",
    "receiver": "B",
    "amount": 100
}'
```