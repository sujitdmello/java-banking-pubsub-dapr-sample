# A Banking Workflow AKS Cluster using DAPR [WIP]

This sample creates an AKS Cluster and uses DAPR for service mesh. The project is built using [Azure Developer CLI](https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/make-azd-compatible?pivots=azd-create) conventions.

## How it works?

This sample implements a simple banking workflow:

1. Public API endpoint receives new money transfer request. [TRANSFER(Sender: A, Amount: 100, Receiver:B)]
1. Request is published to Redis (pub/sub)
1. Deposit workflow starts
    1. Fraud service checks the legitimacy of the operation and triggers [VALIDATED(Sender: A, Amount: 100, Receiver:B)]
    1. Account service checks if `Sender` has enough funds and triggers [APPROVED(Sender: A, Amount: 100, Receiver: B)]
    1. Notification services notifies both `Sender` and `Receiver`.
1. In the meantime, Public API endpoint checks if there is a confirmation of the money transfer request in the notifications.

![Workflow](/docs/flow.drawio.png)

## Services

The project contains the following services:

- [Public API](/src/public-api-service) - Public API endpoint that receives new money transfer requests, starts workflow and checks customer notifications.
- [Fraud Service](/src/fraud-service) - Fraud service that checks the legitimacy of the operation.
- [Account Service](/src/account-service) - Account service that checks if `Sender` has enough funds.
- [Notification Service](/src/notification-service) - Notification service that notifies both `Sender` and `Receiver` by updating/inserting transfer requests in the Public APIs state store.

## Prerequisites

Following technologies and CLIs are used for the development. Follow the links to install them:

- [Azure Developer CLI](https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/make-azd-compatible?pivots=azd-create)
- [Dapr CLI](https://docs.dapr.io/getting-started/install-dapr-cli/)
- [Docker](https://docs.docker.com/get-docker/)
- [Kubernetes](https://kubernetes.io/docs/tasks/tools/)
- [Helm](https://helm.sh/docs/intro/install/)
- [Redis](https://learn.microsoft.com/en-us/azure/azure-cache-for-redis/)
- [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/)
- [Spring Boot](https://spring.io/projects/spring-boot)

## Local Dev Environment Setup

Local environment is setup using [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/). Kind is a tool for running local Kubernetes clusters using Docker container "nodes". Kind was primarily designed for testing Kubernetes itself, but may be used for local development or CI.

### 1. Local Environment Setup

Running following script setups your local environment for development:

```bash
./start-local-env.sh
```

This script does the following:

1. Creates a local Docker registry called `kind-registry` running locally on port 9999.
1. Creates a Kind cluster called `azd-aks` using config file from [kind-cluster-config.yaml](/local/kind-cluster-config.yaml).
1. Connects the registry to the cluster network if not already connected so deployments can access the local registry.
1. Maps the local registry to the cluster
1. Deploys [Redis](https://learn.microsoft.com/en-us/azure/azure-cache-for-redis/) to the cluster using [Helm](https://helm.sh/docs/intro/quickstart/) [chart](https://bitnami.com/stack/redis/helm) to use for different Dapr components (pub/sub, state store, etc).
1. Deploys [Dapr](https://docs.dapr.io/operations/hosting/kubernetes/kubernetes-deploy/) on your local cluster.
1. Deploys [Pub/Sub Broker](https://docs.dapr.io/developing-applications/building-blocks/pubsub/pubsub-overview/) using Redis as the message broker using [redis.yaml](./local/components/redis.yaml) Dapr component.

Your local cluster will be laid out as follows:

![Local](/docs/local.drawio.png)

### 2. Dapr Dashboard & Components

You can validate that the setup finished successfully by navigating to <http://localhost:9000>. This will open the [Dapr dashboard](/docs/dapr-dashboard.png) in your browser.

```bash
dapr dashboard -k -p 9000
```

To verify the installation of pub/sub broker and other components:

```bash
dapr components -k
```

### 3. Deploy Services to Cluster

By convention, every service under `/src` folder has 2 files:

1. [Dockerfile](/src/public-api-service/Dockerfile) - Dockerfile for building the service image.
1. [local-deploy.sh](/src/public-api-service/local-deploy.sh) - Script file to build, publish and deployment the latest code to local cluster as Docker image.

To deploy all services to the cluster, run the following command:

```bash
./deploy-services-local.sh
```

You can check the deployment status of the services:

```bash
k get pods -A
```

You should get an output similar to:

```bash
NAMESPACE            NAME                                            READY   STATUS    RESTARTS       AGE
dapr-system          dapr-dashboard-575df59d4c-mp262                 1/1     Running   0              172m
dapr-system          dapr-operator-676b7df68d-xwzjw                  1/1     Running   0              172m
dapr-system          dapr-placement-server-0                         1/1     Running   1 (171m ago)   172m
dapr-system          dapr-sentry-5f44fd7c9d-gjjcl                    1/1     Running   0              172m
dapr-system          dapr-sidecar-injector-c66df4c49-h645k           1/1     Running   0              172m
default              account-service-655448db67-vdc6n                2/2     Running   0              14m
default              custody-service-5b8656d84c-lgb8j                2/2     Running   0              13m
default              fraud-service-7dfcd56d86-hs7s6                  2/2     Running   0              15m
default              notification-service-7dfbb47b4f-sthln           2/2     Running   0              12m
default              public-api-service-5df9f84648-gb9t2             2/2     Running   0              15m
default              redis-master-0                                  1/1     Running   0              172m
default              redis-replicas-0                                1/1     Running   0              172m
default              redis-replicas-1                                1/1     Running   0              171m
default              redis-replicas-2                                1/1     Running   0              170m
kube-system          coredns-565d847f94-k5nc4                        1/1     Running   0              172m
kube-system          coredns-565d847f94-qx2tn                        1/1     Running   0              172m
kube-system          etcd-azd-aks-control-plane                      1/1     Running   0              172m
kube-system          kindnet-7rhdn                                   1/1     Running   0              172m
kube-system          kindnet-bbk74                                   1/1     Running   0              172m
kube-system          kindnet-nfgrd                                   1/1     Running   0              172m
kube-system          kube-apiserver-azd-aks-control-plane            1/1     Running   0              172m
kube-system          kube-controller-manager-azd-aks-control-plane   1/1     Running   0              172m
kube-system          kube-proxy-6s47w                                1/1     Running   0              172m
kube-system          kube-proxy-7mnzh                                1/1     Running   0              172m
kube-system          kube-proxy-kj8pn                                1/1     Running   0              172m
kube-system          kube-scheduler-azd-aks-control-plane            1/1     Running   0              172m
local-path-storage   local-path-provisioner-684f458cdd-wtmqh         1/1     Running   0              172m
```

### 4. Connecting to Public API Service

[Public API](/src/public-api-service) is deployed as `Loadbalancer` service type. In local cluster setup, it will not able to get public IP to connect.
Instead, you can access this service locally using the Kubectl proxy tool.

```bash
k port-forward service/public-api-service 8080:80
```

While this command is running, you can access the service at <http://localhost:8080>.

An example request to start a new transfer workflow is:

```curl
curl -X POST \
  http://localhost:8080/transfers \
  -H 'Content-Type: application/json' \
  -d '{
    "sender": "A",
    "receiver": "B",
    "amount": 100
}'
```

You can query the status of a transfer:

```curl
curl -X GET \
  http://localhost:8080/transfers/{transferId} \
  -H 'Content-Type: application/json'
```

### 5. Delete Local Cluster

If you'd like to create a clean state and start over, you can run the following command to delete the local cluster and all the resources associated with it.

```bash
kind delete cluster --name azd-aks
```

## Implementation Status

- [X] Public API endpoint receives new money transfer request. [TRANSFER(Sender: A, Amount: 100, Receiver:B)]
- [X] Request is published to Redis (pub/sub)
- [X] Deposit workflow starts
  - [X] Fraud service checks the legitimacy of the operation and triggers [VALIDATED(Sender: A, Amount: 100, Receiver:B)]
  - [X] Account service checks if `Sender` has enough funds and triggers [APPROVED(Sender: A, Amount: 100, Receiver: B)]
  - [ ] Notification services notifies both `Sender` and `Receiver`.
- [ ] In the meantime, Public API endpoint checks if there is a confirmation of the money transfer request in the notifications.


## How To

Following are practical operator guides for common tasks.

### How to Operate Redis

To connect to Redis, you can use the following command:

```bash
kubectl exec -it redis-master-0 -- redis-cli
```

To get Redis password:

```bash
kubectl get secret --namespace default redis -o jsonpath="{.data.redis-password}" | base64 --decode
```

To AUTH with Redis instance for further operations

```bash
AUTH <password>
```

Get all keys in the state store:

```bash
keys *
```