package com.example.showcase.workflow;

import com.example.showcase.api.Requests.OnboardingRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OnboardingWorkflow {
  @WorkflowMethod
  FlowResult run(OnboardingRequest request);
}
