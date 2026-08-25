package com.example.showcase.workflow

import com.example.showcase.activity.clientUpdate.ClientUpdateActivity
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.customerProvisioning.CustomerProvisioningActivity
import com.example.showcase.activity.validation.ValidationActivity
import com.example.showcase.activity.welcomeEmail.WelcomeEmailActivity
import com.example.showcase.api.OnboardingRequest

class OnboardingWorkflowImpl : OnboardingWorkflow {
    private val validation = WorkflowSettings.activity(ValidationActivity::class.java)
    private val customerProvisioning = WorkflowSettings.activity(CustomerProvisioningActivity::class.java)
    private val welcomeEmail = WorkflowSettings.activity(WelcomeEmailActivity::class.java)
    private val clientUpdate = WorkflowSettings.activity(ClientUpdateActivity::class.java)

    override fun run(request: OnboardingRequest): FlowResult {
        val requestId = requireNotNull(request.requestId)
        val context = RequestContext(requestId)
        return try {
            validation.validate(context)
            customerProvisioning.provision(context, request)
            welcomeEmail.send(context, request.customerEmail)
            clientUpdate.update(context, "SUCCESS", "Onboarding completed")
            FlowResult(requestId, "SUCCESS", "Onboarding completed")
        } catch (failure: RuntimeException) {
            clientUpdate.update(context, "FAILED", failure.message)
            FlowResult(requestId, "FAILED", failure.message)
        }
    }
}
