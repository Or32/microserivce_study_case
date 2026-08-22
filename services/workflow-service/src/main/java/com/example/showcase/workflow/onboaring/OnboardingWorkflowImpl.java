package com.example.showcase.workflow;

import com.example.showcase.activity.clientUpdate.ClientUpdateActivity;
import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.customerProvisioning.CustomerProvisioningActivity;
import com.example.showcase.activity.validation.ValidationActivity;
import com.example.showcase.activity.welcomeEmail.WelcomeEmailActivity;
import com.example.showcase.api.Requests.OnboardingRequest;

public class OnboardingWorkflowImpl implements OnboardingWorkflow {
  private final ValidationActivity validation = WorkflowSettings.activity(ValidationActivity.class);
  private final CustomerProvisioningActivity customerProvisioning =
      WorkflowSettings.activity(CustomerProvisioningActivity.class);
  private final WelcomeEmailActivity welcomeEmail = WorkflowSettings.activity(WelcomeEmailActivity.class);
  private final ClientUpdateActivity clientUpdate = WorkflowSettings.activity(ClientUpdateActivity.class);

  @Override
  public FlowResult run(OnboardingRequest request) {
    RequestContext context = new RequestContext(request.requestId());
    try {
      validation.validate(context);
      customerProvisioning.provision(context, request);
      welcomeEmail.send(context, request.customerEmail());
      clientUpdate.update(context, "SUCCESS", "Onboarding completed");
      return new FlowResult(request.requestId(), "SUCCESS", "Onboarding completed");
    } catch (RuntimeException failure) {
      clientUpdate.update(context, "FAILED", failure.getMessage());
      return new FlowResult(request.requestId(), "FAILED", failure.getMessage());
    }
  }
}
