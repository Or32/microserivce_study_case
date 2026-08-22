# Temporal + Micronaut request-flow showcase

This is a multi-service Micronaut + Temporal project. `workflow-service` exposes the HTTP API and runs only workflow orchestration. Every activity runs in its own independently deployable Micronaut worker service.

| Flow | Shared steps | Unique step |
| --- | --- | --- |
| Onboarding | validate, welcome email, client update | provision customer |
| Payment | validate, receipt, client update | charge payment |

Every activity receives `RequestContext`; `RequestLogger` adds `requestId=...` to every log line. The final client-update activity is deliberately executed for both success and failure paths (it is a log-only mock client).

## Repository layout

```text
infrastructure/
  temporal/                       Local Temporal server and UI Compose setup
services/
  workflow-service/               HTTP API + onboarding/payment workflow worker
  validation-service/             Validation activity worker
  customer-provisioning-service/  Customer provisioning activity worker
  welcome-email-service/          Welcome-email activity worker
  payment-charging-service/       Payment-charging activity worker
  receipt-service/                Receipt activity worker
  client-update-service/          Final client-update activity worker
shared/
  activity-contracts/             Activity interfaces, request types, and workflow results
  activity-utils/                 Activity worker lifecycle and request-logging helpers
```

## Run

Build and start the entire system: Temporal, its UI at <http://localhost:8081>, Loki, Grafana at <http://localhost:3000>, the workflow API, and all activity services:

```bash
make infra-up
```

Useful infrastructure shortcuts:

```bash
make infra-status  # Show containers
make infra-logs    # Follow logs
make infra-down    # Stop containers
make infra-kill    # Force-stop and remove containers
```

The API is available at <http://localhost:8080> once all containers are healthy. For local development without Docker, build the shared modules once, then run the services manually:

```bash
./mvnw clean install -DskipTests

SERVICE_NAME=workflow-service ./mvnw -pl services/workflow-service mn:run
SERVICE_NAME=validation-service ./mvnw -pl services/validation-service mn:run
SERVICE_NAME=customer-provisioning-service ./mvnw -pl services/customer-provisioning-service mn:run
SERVICE_NAME=welcome-email-service ./mvnw -pl services/welcome-email-service mn:run
SERVICE_NAME=payment-charging-service ./mvnw -pl services/payment-charging-service mn:run
SERVICE_NAME=receipt-service ./mvnw -pl services/receipt-service mn:run
SERVICE_NAME=client-update-service ./mvnw -pl services/client-update-service mn:run
```

Then start a flow:

```bash
curl -X POST localhost:8080/requests/onboarding -H 'Content-Type: application/json' \
  -d '{"requestId":"onboard-101","customerEmail":"ada@example.com"}'

curl -X POST localhost:8080/requests/payments -H 'Content-Type: application/json' \
  -d '{"requestId":"retry-payment-101","customerId":"cust-9","amountCents":1250}'
```

Use a `requestId` beginning with `retry-` to see one transient validation retry. Use one beginning with `critical-` to produce a non-retryable business failure; the client still gets its final `FAILED` update. These prefixes exist only to make the behavior easy to demonstrate.

## Centralized logs

Open Grafana at <http://localhost:3000>, choose **Explore**, and select the preconfigured **Loki** source. Start with:

```logql
{service="payment-charging-service"}
```

To find every activity log for one request, query its structured metadata:

```logql
{service=~".+"} | requestId = "retry-payment-101"
```

`SERVICE_NAME` is a low-cardinality Loki label, while `requestId` is structured metadata. This keeps Loki indexes small while still making an individual request searchable.

## Send test requests

With `workflow-service` running on port 8080:

```bash
make run-workflow  # Run this in a separate terminal
make mock-onboarding
make mock-payment
make mock-retry
make mock-critical
```

Override the defaults when needed:

```bash
make mock-payment REQUEST_ID=payment-42 CUSTOMER_ID=customer-42 AMOUNT_CENTS=5000
```

## Design notes

- Every activity service listens only to its own Temporal task queue. This prevents, for example, the email worker from receiving payment activity tasks.
- `WorkflowSettings` applies exponential retry (three attempts) and explicitly lists `CriticalBusinessException` as non-retryable. Activities also create this error as Temporal's non-retryable `ApplicationFailure`, which prevents retry at the source.
- Workflow code is deterministic orchestration only. Logging and the mock client call happen inside activity services.
