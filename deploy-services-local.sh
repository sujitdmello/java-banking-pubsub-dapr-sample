#!/bin/sh
set -o errexit

printf "\n🤖  Starting local deployments...\n\n"

printf '\n🎖️  Deploying Public API Service\n\n'
cd ./src/public-api-service
sh ./local-deploy.sh

printf '\n ================================== \n\n'

printf '\n🎖️  Deploying Fraud Service\n\n'
cd ../../src/fraud-service
sh ./local-deploy.sh