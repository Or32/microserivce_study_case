package com.example.showcase.activity.customerProvisioning;

import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.util.RequestLogger;
import com.example.showcase.api.Requests.OnboardingRequest;
import jakarta.inject.Singleton;

@Singleton
public class CustomerProvisioningActivityImpl implements CustomerProvisioningActivity {
  private static final RequestLogger LOG =
      RequestLogger.forClass(CustomerProvisioningActivityImpl.class);

  @Override
  public void provision(RequestContext context, OnboardingRequest request) {
    LOG.info(context, "Customer provisioned for {}", request.customerEmail());
  }
}
