#!/bin/sh
set -o errexit

# Get EXTERNAL_IP from kubectl
AKS_PUBLIC_IP=$(kubectl get service public-api-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

transfer=$(curl -X POST \
  http://${AKS_PUBLIC_IP}/transfers \
  -H 'Content-Type: application/json' \
  -d '{
    "sender": "A",
    "receiver": "B",
    "amount": 100
}' | jq)

echo $transfer

transferId=$(echo $transfer | jq -r '.transferId')
echo "TransferId: $transferId"

curl -X GET \
  http://${AKS_PUBLIC_IP}/transfers/${transferId} \
  -H 'Content-Type: application/json' | jq