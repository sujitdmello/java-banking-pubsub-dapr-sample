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

test: ## 🧪 Run tests, used for local development
	@echo -e "\e[34m$@\e[0m" || true
	@./test.sh

deploy: ## 🚀 Deploy application resources
	@echo -e "\e[34m$@\e[0m" || true
	@./deploy-services-azure.sh

clean: ## 🧹 Clean up local files
	@echo -e "\e[34m$@\e[0m" || true
	@echo "Delete docker images here"