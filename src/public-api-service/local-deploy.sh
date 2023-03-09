#!/bin/sh

version=$(date +%Y.%m.%d.%H.%M.%S)
printf "🛖 Releasing version: %s\n" "${version}"

printf "🗑️ Attempting to delete existing deployment public-api-service\n"
kubectl delete deployment public-api-service

printf "🏗️ Building docker image\n"
docker build -t localhost:5001/public-api-service:"${version}" .

printf "🚚 Pushing docker image to local registry\n"
docker push localhost:5001/public-api-service:"${version}"

printf "🚀 Deploying to cluster\n"
cat <<EOF | kubectl apply -f -

kind: Service
apiVersion: v1
metadata:
  name: public-api-service
  labels:
    app: public-api-service
spec:
  selector:
    app: public-api-service
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: public-api-service
  labels:
    app: public-api-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: public-api-service
  template:
    metadata:
      labels:
        app: public-api-service
      annotations:
        dapr.io/enabled: "true"
        dapr.io/app-id: "money-transfer-app"
        dapr.io/app-port: "8080"
        dapr.io/enable-api-logging: "true"
    spec:
      containers:
      - name: node
        image: localhost:5001/public-api-service:${version}
        env:
        - name: APP_PORT
          value: "8080"
        ports:
        - containerPort: 80
        imagePullPolicy: Always
EOF