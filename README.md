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

- [Public API](/src/public-api) - Public API endpoint that receives new money transfer requests, starts workflow and checks customer notifications.
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

You can validate that the setup finished successfully by navigating to http://localhost:9999. This will open the [Dapr dashboard](/docs/dapr-dashboard.png) in your browser.

```bash
dapr dashboard -k -p 9999
```