package com.example.showcase.api

import com.example.showcase.temporal.TemporalWorkerConfiguration
import com.example.showcase.workflow.OnboardingWorkflow
import com.example.showcase.workflow.PaymentWorkflow
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import java.util.UUID

@Controller("/requests")
class FlowController(private val temporal: TemporalWorkerConfiguration) {
    @Post("/onboarding")
    fun startOnboarding(@Body body: OnboardingRequest): HttpResponse<StartedResponse> {
        val requestId = requestId(body.requestId)
        val workflow = temporal.workflowClient.newWorkflowStub(OnboardingWorkflow::class.java, options("onboarding", requestId))
        WorkflowClient.start(workflow::run, OnboardingRequest(requestId, body.customerEmail))
        return HttpResponse.status<StartedResponse>(HttpStatus.ACCEPTED).body(StartedResponse("onboarding-$requestId", requestId))
    }

    @Post("/payments")
    fun startPayment(@Body body: PaymentRequest): HttpResponse<StartedResponse> {
        val requestId = requestId(body.requestId)
        val workflow = temporal.workflowClient.newWorkflowStub(PaymentWorkflow::class.java, options("payment", requestId))
        WorkflowClient.start(workflow::run, PaymentRequest(requestId, body.customerId, body.amountCents))
        return HttpResponse.status<StartedResponse>(HttpStatus.ACCEPTED).body(StartedResponse("payment-$requestId", requestId))
    }

    private fun options(flow: String, requestId: String) = WorkflowOptions.newBuilder()
        .setTaskQueue(temporal.taskQueue)
        .setWorkflowId("$flow-$requestId")
        .build()

    private fun requestId(candidate: String?) = candidate?.takeUnless { it.isBlank() } ?: UUID.randomUUID().toString()
}
