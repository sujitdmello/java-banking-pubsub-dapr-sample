# Public API

This is the public API for starting money transfers.

## Build the app

```bash
docker build -t azd-aks/public-api-service:latest .
```

## Run the app

```bash
dapr run --app-id money-tansfer --app-port 6001 --dapr-http-port 3601 --dapr-grpc-port 60001 ./gradlew bootRun
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