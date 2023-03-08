# A Banking Workflow AKS Cluster using DAPR

This sample creates an AKS Cluster and uses DAPR for service mesh. The project is built using [Azure Developer CLI](https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/make-azd-compatible?pivots=azd-create) conventions.

## How it works?

This sample implements a simple banking workflow:

1. Public API endpoint receives new money transfer request. [TRANSFER(Sender: A, Amount: 100, Receiver:B)]
1. Request is published to Redis (pub/sub)
1. Deposit workflow starts
    1. Fraud service checks the legitimacy of the operation and triggers [VALIDATED(Sender: A, Amount: 100, Receiver:B)]
    1. Account service checks if `Sender` has enough funds and triggers [APPROVED(Sender: A, Amount: 100, Receiver: B)]
    1. Custody service moves `Amount` from `Sender` to `Receiver` and triggers [COMPLETED(Sender: A, Amount: 100, Receiver: B)]
    1. Notification services notifies both `Sender` and `Receiver`.
1. In the meantime, Public API endpoint checks if there is a confirmation of the money transfer request in the notifications.

![Workflow](/docs/flow.drawio.png)

## Services

The project contains the following services:

- [Public API](/src/public-api-service) - Public API endpoint that receives new money transfer requests, starts workflow and checks customer notifications.
- [Fraud Service](/src/fraud-service) - Fraud service that checks the legitimacy of the operation.
- [Account Service](/src/account-service) - Account service that checks if `Sender` has enough funds.
- [Custody Service](/src/custody-service) - Custody service that moves `Amount` from `Sender` to `Receiver`.
- [Notification Service](/src/notification-service) - Notification service that notifies both `Sender` and `Receiver` by updating/inserting transfer requests in the Public APIs database.

## Prerequisites

Following technologies and CLIs are used for the development. Follow the links to install them:

- [Azure Developer CLI](https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/make-azd-compatible?pivots=azd-create)
- [Dapr CLI](https://docs.dapr.io/getting-started/install-dapr-cli/)
- [Dapr for AKS](https://docs.dapr.io/getting-started/install-dapr-kubernetes/)
- [Redis](https://learn.microsoft.com/en-us/azure/azure-cache-for-redis/)
- [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/)
- [Spring Boot](https://spring.io/projects/spring-boot)

## Local Dev Environment Setup

Local environment is setup using [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/). Kind is a tool for running local Kubernetes clusters using Docker container "nodes".

Make an alias for `kubectl` so the next set of commands are easier to read:

```bash
alias k='kubectl'
```

### 0. Local Image Registry

One of the challenges in using Kubernetes for local development is getting local Docker containers you create during development to your Kubernetes cluster. Configuring this correctly allows Kubernetes to access any Docker images you create locally when deploying your Pods and Services to the cluster you created using kind.

The following script creates a Docker registry called `kind-registry` running locally on port 9999. This script first inspects the current environment to check if we already have a local registry running, and if we do not, then we start a new registry. The registry itself is simply an instance of the registry Docker image available on Docker Hub. We use the docker run command to start the registry.

```bash
./local/create-registry.sh
```

### 1. Create Kind Cluster (Local k8s Cluster)

Run following to create a Kind cluster called `azd-aks` using config file from [kind-cluster-config.yaml](/local/kind-cluster-config.yaml):

```bash
kind create cluster --name azd-aks --config ./local/kind-cluster-config.yaml
```

This is going to request KiND to spin up a kubernetes cluster comprised of a control plane and two worker nodes.
It also allows for future setup of ingresses and exposes container ports to the host machine.

```bash
## Get all pods from all namespaces including system pods
k get pods -A
```

This should output something like below or similar to [docker desktop screenshot](./docs//kind-cluster-running.png):

```bash
NAMESPACE            NAME                                            READY   STATUS    RESTARTS   AGE
kube-system          coredns-565d847f94-l766m                        1/1     Running   0          13s
kube-system          coredns-565d847f94-w679q                        1/1     Running   0          13s
kube-system          etcd-azd-aks-control-plane                      1/1     Running   0          28s
kube-system          kindnet-9p8wc                                   1/1     Running   0          13s
kube-system          kindnet-n94pz                                   1/1     Running   0          10s
kube-system          kindnet-sfsgr                                   1/1     Running   0          10s
kube-system          kube-apiserver-azd-aks-control-plane            1/1     Running   0          29s
kube-system          kube-controller-manager-azd-aks-control-plane   1/1     Running   0          26s
kube-system          kube-proxy-nrbv6                                1/1     Running   0          13s
kube-system          kube-proxy-vxh62                                1/1     Running   0          10s
kube-system          kube-proxy-wss4d                                1/1     Running   0          10s
kube-system          kube-scheduler-azd-aks-control-plane            1/1     Running   0          28s
local-path-storage   local-path-provisioner-684f458cdd-t2fp6         1/1     Running   0          13s
```

The last step we have is to connect the kind cluster’s network with the local Docker registry’s network:

```bash
docker network connect "kind" "kind-registry"
 ```


### 2. Install Dapr on Kind Cluster

Once Dapr finishes initializing its core components are ready to be used on the cluster. Run the following command to install Dapr on the Kind cluster:

```bash
dapr init --kubernetes
```

This should output something like below:

```bash
⌛  Making the jump to hyperspace...
ℹ️  Note: To install Dapr using Helm, see here: https://docs.dapr.io/getting-started/install-dapr-kubernetes/#install-with-helm-advanced

ℹ️  Container images will be pulled from Docker Hub
✅  Deploying the Dapr control plane to your cluster...
✅  Success! Dapr has been installed to namespace dapr-system. To verify, run `dapr status -k' in your terminal. To get started, go here: https://aka.ms/dapr-getting-started
```

To verify the status of these components run:

```bash
dapr status -k
```

This should output something like below:

```bash
NAME                   NAMESPACE    HEALTHY  STATUS   REPLICAS  VERSION  AGE  CREATED              
dapr-dashboard         dapr-system  True     Running  1         0.12.0   10m  2023-03-07 14:35.06  
dapr-sentry            dapr-system  True     Running  1         1.10.2   10m  2023-03-07 14:35.06  
dapr-placement-server  dapr-system  True     Running  1         1.10.2   10m  2023-03-07 14:35.07  
dapr-operator          dapr-system  True     Running  1         1.10.2   10m  2023-03-07 14:35.06  
dapr-sidecar-injector  dapr-system  True     Running  1         1.10.2   10m  2023-03-07 14:35.06  
```

You can validate that the setup finished successfully by navigating to <http://localhost:9000>. This will open the [Dapr dashboard](/docs/dapr-dashboard.png) in your browser.

```bash
dapr dashboard -k -p 9000
```

### 3. Deploy Dapr Pub/Sub Broker (Redis)

Dapr Pub/Sub Broker uses [Redis](https://redis.io/) as a pub/sub broker.
A `redis.yaml` under `./local/components` folder to define Pub/Sub Broker was created in the repo.
Run the following command to deploy Redis as Pub/Sub Broker on the Kind cluster:

```bash
k apply -f ./local/components/redis.yaml
```

To verify the installation of pub/sub broker run:

```bash
dapr components -k
```

This should output something similar to below. Alternatively, you can check the status of the component in the Dapr dashboard `http://localhost:9999/components`:

```bash
NAMESPACE  NAME    TYPE          VERSION  SCOPES  CREATED              AGE  
default    pubsub  pubsub.redis  v1               2023-03-07 15:02.34  3m  
```

### 4. Deploy Public API Service to Cluster

We will deploy a public API service to the cluster. This service will be exposed as load balancer service type.

#### 4.1. Build and Push Docker Image to Local Registry

You can build public-api-service as a Docker container using the docker build command.
The following line tags the build using the -t flag and specifies the local repository we created earlier.

```bash
cd ./src/public-api-service
docker build -t localhost:5001/public-api-service:latest .
```

At this point, we have a Docker container built and tagged. Next we can push it to our local repository with the docker push command.

```bash
docker push localhost:5001/public-api-service:latest
```

#### 4.2 Deploy Public API Service to Cluster

At the root of the project:

```bash
k apply -f ./local/deployments/public-api-service.yaml
```

This will output something like below:

```bash
service/public-api created
deployment.apps/public-api created
```

You can check the deployment status of the public-api service by running:

```bash
k get pods -A
```

#### 4.3. Expose Public API Service

Loadbalancer service type you will not able to get public ip because you're running it locally and instead you can access this service locally using the Kubectl proxy tool.

```bash
k port-forward service/public-api-service 8080:80
```
