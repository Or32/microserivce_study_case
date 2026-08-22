package com.example.showcase.activity.customerProvisioning;

import com.example.showcase.activity.ActivityTaskQueue;
import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.api.Requests.OnboardingRequest;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
@ActivityTaskQueue("customer-provisioning-activities")
public interface CustomerProvisioningActivity {
  void provision(RequestContext context, OnboardingRequest request);
}
