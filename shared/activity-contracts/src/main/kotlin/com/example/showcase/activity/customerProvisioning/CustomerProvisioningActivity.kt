package com.example.showcase.activity.customerProvisioning

import com.example.showcase.activity.ActivityTaskQueue
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.api.OnboardingRequest
import io.temporal.activity.ActivityInterface

@ActivityInterface
@ActivityTaskQueue("customer-provisioning-activities")
interface CustomerProvisioningActivity { fun provision(context: RequestContext, request: OnboardingRequest) }
