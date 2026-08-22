COMPOSE := docker compose -f infrastructure/temporal/docker-compose.yml
MVNW := ./mvnw
API_URL ?= http://localhost:8080
REQUEST_ID ?= demo-001
CUSTOMER_EMAIL ?= ada@example.com
CUSTOMER_ID ?= customer-001
AMOUNT_CENTS ?= 1250

.PHONY: infra-up infra-down infra-kill infra-logs infra-status run-workflow api-ready mock-onboarding mock-payment mock-retry mock-critical

## Build and start the complete system: Temporal, Grafana, and all services.
infra-up:
	$(COMPOSE) up -d --build

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
	SERVICE_NAME=workflow-service $(MVNW) -pl services/workflow-service mn:run

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
