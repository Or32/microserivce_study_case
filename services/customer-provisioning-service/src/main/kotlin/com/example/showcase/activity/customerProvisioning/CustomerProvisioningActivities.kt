package com.example.showcase.activity.customerProvisioning
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.util.ActivityService
import com.example.showcase.activity.util.RequestLogger
import com.example.showcase.api.OnboardingRequest
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
@Singleton class CustomerProvisioningActivityImpl : CustomerProvisioningActivity {
    private val log = RequestLogger.forClass(CustomerProvisioningActivityImpl::class.java)
    override fun provision(context: RequestContext, request: OnboardingRequest) { log.info(context, "Customer provisioned for {}", request.customerEmail) }
}
@Context @Singleton class CustomerProvisioningActivityService(@Value("\${temporal.target}") target: String, @Value("\${temporal.namespace}") namespace: String, @Value("\${temporal.task-queue}") taskQueue: String, activity: CustomerProvisioningActivityImpl) : ActivityService() { init { startWorker(target, namespace, taskQueue, activity) } }
