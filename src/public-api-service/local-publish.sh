#!/bin/sh

echo "Whats the version number?"
read version

kubectl delete deployment public-api-service

docker build -t localhost:5001/public-api-service:${version} .

docker push localhost:5001/public-api-service:${version}

kubectl apply -f ../../local/deployments/public-api-service.yaml