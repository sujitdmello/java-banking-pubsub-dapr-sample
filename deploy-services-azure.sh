#!/bin/sh
set -o errexit

. ./.env
# If no access to Azure then display error message telling azure to login
if ! az account show 1>/dev/null 2>&1; then
  az login --tenant $ARM_TENANT_ID
fi

az account set --subscription $ARM_SUBSCRIPTION_ID

printf "\n⚓  Getting K8s Context...\n\n"
az aks get-credentials --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME

printf "\n🤖  Starting Azure deployments...\n\n"

printf '\n📀 Deploy Redis\n\n'
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install redis bitnami/redis

printf '\n📀 Init Darp\n\n'
dapr init --kubernetes --wait --timeout 600

printf '\n🎖️  Deploying Public API Service\n\n'
cd ./src/public-api-service
sh ./azure-deploy.sh

printf '\n ================================== \n\n'

printf '\n🎖️  Deploying Fraud Service\n\n'
cd ../../src/fraud-service
sh ./azure-deploy.sh

printf '\n ================================== \n\n'

printf '\n🎖️  Deploying Account Service\n\n'
cd ../../src/account-service
sh ./azure-deploy.sh

printf '\n ================================== \n\n'

printf '\n🎖️  Notification Service\n\n'
cd ../../src/notification-service
sh ./azure-deploy.sh
