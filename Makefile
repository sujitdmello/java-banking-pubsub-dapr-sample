SHELL := /bin/bash

VERSION := 0.0.1
BUILD_INFO := Manual build

ENV_FILE := .env
ifeq ($(filter $(MAKECMDGOALS),config clean),)
	ifneq ($(strip $(wildcard $(ENV_FILE))),)
		ifneq ($(MAKECMDGOALS),config)
			include $(ENV_FILE)
			export
		endif
	endif
endif

.PHONY: help lint image push build run
.DEFAULT_GOAL := help

help: ## 💬 This help message :)
	@grep -E '[a-zA-Z_-]+:.*?## .*$$' $(firstword $(MAKEFILE_LIST)) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

all: deploy test ## 🏃‍♀️ Run all the things

test: ## 🧪 Run tests, used for azure development
	@echo -e "\e[34m$@\e[0m" || true
	@./scripts/test.sh --azure

deploy: ## 🚀 Deploy application resources
	@echo -e "\e[34m$@\e[0m" || true
	@./scripts/deploy-services-azure.sh

clean: ## 🧹 Clean up local files
	@echo -e "\e[34m$@\e[0m" || true
	@kind delete cluster --name azd-aks

start-local: ## 🧹 Setup local Kind Cluster
	@echo -e "\e[34m$@\e[0m" || true
	@./scripts/start-local-env.sh

port-forward-local: ## ⏩ Forward the local port
	@echo -e "\e[34m$@\e[0m" || true
	@kubectl port-forward service/public-api-service 8080:80

deploy-local: ## 🚀 Deploy application resources
	@echo -e "\e[34m$@\e[0m" || true
	@./scripts/deploy-services-local.sh

test-local: ## 🧪 Run tests, used for local development
	@echo -e "\e[34m$@\e[0m" || true
	@./scripts/test.sh
