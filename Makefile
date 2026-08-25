COMPOSE := docker compose -f infrastructure/temporal/docker-compose.yml
JAVA_21_HOME := /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export JAVA_HOME := $(JAVA_21_HOME)
export PATH := $(JAVA_HOME)/bin:$(PATH)
MVNW := ./mvnw
API_URL ?= http://localhost:8080
REQUEST_ID ?= demo-001
CUSTOMER_EMAIL ?= ada@example.com
CUSTOMER_ID ?= customer-001
AMOUNT_CENTS ?= 1250

.PHONY: infra-up infra-offline-up infra-local-up infra-down infra-kill infra-logs infra-status offline-bundle offline-install-maven-cache run-workflow run-validation run-customer-provisioning run-welcome-email run-payment-charging run-receipt run-client-update api-ready mock-onboarding mock-payment mock-retry mock-critical

## Build and start the complete system: Temporal, Grafana, and all services.
infra-up:
	$(COMPOSE) up -d --build

## Start preloaded infrastructure images without building or pulling; intended for an air-gapped host.
infra-offline-up:
	$(COMPOSE) up -d --no-build --pull never temporal temporal-postgresql temporal-ui loki grafana

## Start only Temporal and its UI for locally run services.
infra-local-up:
	$(COMPOSE) up -d temporal temporal-ui

## Create a transferable bundle with the Maven cache and infrastructure container images.
offline-bundle:
	./scripts/prepare-offline-bundle.sh

## Install Maven artifacts from OFFLINE_BUNDLE (default: ./offline-bundle) into ~/.m2.
offline-install-maven-cache:
	./scripts/install-offline-maven-cache.sh "$(OFFLINE_BUNDLE)"

## Stop the complete system and preserve volumes.
infra-down:
	$(COMPOSE) down

## Force-stop and remove all system containers, including orphaned containers.
infra-kill:
	$(COMPOSE) down --remove-orphans

## Follow logs from every container in the system.
infra-logs:
	$(COMPOSE) logs -f

## Show the state of all system containers.
infra-status:
	$(COMPOSE) ps

## Start workflow-service locally instead of inside Docker.
run-workflow:
	SERVICE_NAME=workflow-service TEMPORAL_TARGET=localhost:7233 $(MVNW) -pl services/workflow-service mn:run -Dexec.mainClass=com.example.showcase.Application

run-validation:
	SERVICE_NAME=validation-service TEMPORAL_TARGET=localhost:7233 $(MVNW) -pl services/validation-service mn:run -Dexec.mainClass=com.example.showcase.ValidationApplication

run-customer-provisioning:
	SERVICE_NAME=customer-provisioning-service TEMPORAL_TARGET=localhost:7233 $(MVNW) -pl services/customer-provisioning-service mn:run -Dexec.mainClass=com.example.showcase.CustomerProvisioningApplication

run-welcome-email:
	SERVICE_NAME=welcome-email-service TEMPORAL_TARGET=localhost:7233 $(MVNW) -pl services/welcome-email-service mn:run -Dexec.mainClass=com.example.showcase.WelcomeEmailApplication

run-payment-charging:
	SERVICE_NAME=payment-charging-service TEMPORAL_TARGET=localhost:7233 $(MVNW) -pl services/payment-charging-service mn:run -Dexec.mainClass=com.example.showcase.PaymentChargingApplication

run-receipt:
	SERVICE_NAME=receipt-service TEMPORAL_TARGET=localhost:7233 $(MVNW) -pl services/receipt-service mn:run -Dexec.mainClass=com.example.showcase.ReceiptApplication

run-client-update:
	SERVICE_NAME=client-update-service TEMPORAL_TARGET=localhost:7233 $(MVNW) -pl services/client-update-service mn:run -Dexec.mainClass=com.example.showcase.ClientUpdateApplication

## Check that workflow-service is listening before sending a mock request.
api-ready:
	@curl --silent --show-error --connect-timeout 1 --max-time 2 $(API_URL)/ > /dev/null || { echo "workflow-service is not running at $(API_URL). Start it with: make run-workflow"; exit 1; }

## Start a successful onboarding workflow.
mock-onboarding: api-ready
	curl --fail-with-body -X POST $(API_URL)/requests/onboarding -H 'Content-Type: application/json' -d '{"requestId":"$(REQUEST_ID)","customerEmail":"$(CUSTOMER_EMAIL)"}'

## Start a successful payment workflow.
mock-payment: api-ready
	curl --fail-with-body -X POST $(API_URL)/requests/payments -H 'Content-Type: application/json' -d '{"requestId":"$(REQUEST_ID)","customerId":"$(CUSTOMER_ID)","amountCents":$(AMOUNT_CENTS)}'

## Start a payment workflow that fails validation once, then retries successfully.
mock-retry:
	$(MAKE) mock-payment REQUEST_ID=retry-payment-001

## Start a payment workflow with a non-retryable critical validation failure.
mock-critical:
	$(MAKE) mock-payment REQUEST_ID=critical-payment-001
