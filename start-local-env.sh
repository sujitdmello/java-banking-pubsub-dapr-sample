#!/bin/sh
set -o errexit

printf "\n🤖 Starting local environment...\n\n"

printf '\n📀 create registry container unless it already exists\n\n'
reg_name='kind-registry'
reg_port='5001'
if [ "$(docker inspect -f '{{.State.Running}}' "${reg_name}" 2>/dev/null || true)" != 'true' ]; then
  docker run \
    -d --restart=always -p "127.0.0.1:${reg_port}:5000" --name "${reg_name}" \
    registry:2
fi

printf '\n📀 create kind cluster called: azd-aks\n\n'
kind create cluster --name azd-aks --config ./local/kind-cluster-config.yaml

printf '\n📀 connect the registry to the cluster network if not already connected\n'
if [ "$(docker inspect -f='{{json .NetworkSettings.Networks.kind}}' "${reg_name}")" = 'null' ]; then
  docker network connect "kind" "${reg_name}"
fi

printf '\n📀 map the local registry to cluster\n\n'
kubectl apply -f ./local/deployments/config-map.yaml --wait=true

printf '\n📀 init dapr\n\n'
dapr init --kubernetes --wait --timeout 600

printf '\n📀 deploy redis as state store\n\n'
kubectl apply -f ./local/components/redis.yaml --wait=true


printf "\n🎉 Local environment setup completed!\n\n"