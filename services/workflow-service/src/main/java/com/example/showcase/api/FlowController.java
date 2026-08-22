package com.example.showcase.api;

import com.example.showcase.api.Requests.OnboardingRequest;
import com.example.showcase.api.Requests.PaymentRequest;
import com.example.showcase.api.Requests.StartedResponse;
import com.example.showcase.temporal.TemporalWorkerConfiguration;
import com.example.showcase.workflow.OnboardingWorkflow;
import com.example.showcase.workflow.PaymentWorkflow;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import java.util.UUID;

@Controller("/requests")
public class FlowController {
  private final TemporalWorkerConfiguration temporal;

  public FlowController(TemporalWorkerConfiguration temporal) {
    this.temporal = temporal;
  }

  @Post("/onboarding")
  public HttpResponse<StartedResponse> startOnboarding(@Body OnboardingRequest body) {
    String requestId = requestId(body.requestId());
    OnboardingWorkflow workflow =
        temporal
            .workflowClient()
            .newWorkflowStub(OnboardingWorkflow.class, options("onboarding", requestId));
    WorkflowClient.start(workflow::run, new OnboardingRequest(requestId, body.customerEmail()));
    return HttpResponse.status(HttpStatus.ACCEPTED)
        .body(new StartedResponse("onboarding-" + requestId, requestId));
  }

  @Post("/payments")
  public HttpResponse<StartedResponse> startPayment(@Body PaymentRequest body) {
    String requestId = requestId(body.requestId());
    PaymentWorkflow workflow =
        temporal
            .workflowClient()
            .newWorkflowStub(PaymentWorkflow.class, options("payment", requestId));
    WorkflowClient.start(
        workflow::run, new PaymentRequest(requestId, body.customerId(), body.amountCents()));
    return HttpResponse.status(HttpStatus.ACCEPTED)
        .body(new StartedResponse("payment-" + requestId, requestId));
  }

  private WorkflowOptions options(String flow, String requestId) {
    return WorkflowOptions.newBuilder()
        .setTaskQueue(temporal.taskQueue())
        .setWorkflowId(flow + "-" + requestId)
        .build();
  }

  private String requestId(String candidate) {
    return candidate == null || candidate.isBlank() ? UUID.randomUUID().toString() : candidate;
  }
}
