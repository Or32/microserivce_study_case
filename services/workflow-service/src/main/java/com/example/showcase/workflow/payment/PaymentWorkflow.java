package com.example.showcase.workflow;

import com.example.showcase.api.Requests.PaymentRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PaymentWorkflow {
  @WorkflowMethod
  FlowResult run(PaymentRequest request);
}
