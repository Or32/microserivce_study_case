package com.example.showcase.temporal

import com.example.showcase.workflow.OnboardingWorkflowImpl
import com.example.showcase.workflow.PaymentWorkflowImpl
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import io.temporal.worker.WorkerFactory
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton

@Context
@Singleton
class TemporalWorkerConfiguration(
    @Value("\${temporal.target}") target: String,
    @Value("\${temporal.namespace}") namespace: String,
    val taskQueue: String,
) {
    private val service = WorkflowServiceStubs.newServiceStubs(
        WorkflowServiceStubsOptions.newBuilder().setTarget(target).build(),
    )
    val workflowClient = WorkflowClient.newInstance(
        service,
        WorkflowClientOptions.newBuilder().setNamespace(namespace).build(),
    )
    private val workerFactory = WorkerFactory.newInstance(workflowClient)

    init {
        workerFactory.newWorker(taskQueue).registerWorkflowImplementationTypes(
            OnboardingWorkflowImpl::class.java,
            PaymentWorkflowImpl::class.java,
        )
        workerFactory.start()
    }

    @PreDestroy
    fun stop() {
        workerFactory.shutdown()
        service.shutdown()
    }
}
