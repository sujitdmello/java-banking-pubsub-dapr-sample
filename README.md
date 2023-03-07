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
- [Notification Service](/src/notification-service) - Notification service that notifies both `Sender` and `Receiver`.

## Prerequisites

Following technologies and CLIs are used for local development:

- [Azure Developer CLI](https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/make-azd-compatible?pivots=azd-create)
- [Dapr CLI](https://docs.dapr.io/getting-started/install-dapr-cli/)
- [Dapr for AKS](https://docs.dapr.io/getting-started/install-dapr-kubernetes/)
- [Redis](https://learn.microsoft.com/en-us/azure/azure-cache-for-redis/)
- [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/)
- [Spring Boot](https://spring.io/projects/spring-boot)