package com.example.showcase.activity.util

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import io.temporal.worker.WorkerFactory

/** Starts one Temporal worker for one activity implementation. */
class ActivityWorker private constructor(
    private val service: WorkflowServiceStubs,
    private val workerFactory: WorkerFactory,
) : AutoCloseable {
    companion object {
        fun start(target: String, namespace: String, taskQueue: String, activity: Any): ActivityWorker {
            val service = WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(target).build(),
            )
            val client = WorkflowClient.newInstance(
                service,
                WorkflowClientOptions.newBuilder().setNamespace(namespace).build(),
            )
            val factory = WorkerFactory.newInstance(client)
            factory.newWorker(taskQueue).registerActivitiesImplementations(activity)
            factory.start()
            return ActivityWorker(service, factory)
        }
    }

    override fun close() {
        workerFactory.shutdown()
        service.shutdown()
    }
}
