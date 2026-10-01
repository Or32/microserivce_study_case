package com.example.showcase.activity.util

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.api.workflowservice.v1.GetSystemInfoRequest
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import io.temporal.worker.WorkerFactory
import java.util.concurrent.TimeUnit

class ActivityWorker private constructor(
    private val service: WorkflowServiceStubs,
    private val namespace: String,
    private val taskQueue: String,
    private val activity: Any,
    activityChecks: List<ConnectionGateCheck>,
) : AutoCloseable {
    private var workerFactory: WorkerFactory? = null
    private val gate = ConnectionGate(
        checks = listOf(ConnectionGateCheck { isTemporalReachable() }) + activityChecks,
        onOpen = ::startOrResumePolling,
        onClose = ::suspendPolling,
    )

    companion object {
        fun start(
            target: String,
            namespace: String,
            taskQueue: String,
            activity: Any,
            checks: List<ConnectionGateCheck> = emptyList(),
        ): ActivityWorker {
            val service = WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(target).build(),
            )
            val activityChecks: List<ConnectionGateCheck> =
                (activity as? ConnectionGatedActivity)?.connectionGateChecks() ?: emptyList()
            return ActivityWorker(service, namespace, taskQueue, activity, activityChecks + checks).also {
                it.gate.start()
            }
        }
    }

    private fun isTemporalReachable(): Boolean = try {
        service.blockingStub()
            .withDeadlineAfter(2, TimeUnit.SECONDS)
            .getSystemInfo(GetSystemInfoRequest.getDefaultInstance())
        true
    } catch (_: Exception) {
        false
    }

    @Synchronized
    private fun startOrResumePolling(): Boolean {
        val existingFactory = workerFactory
        if (existingFactory != null) {
            existingFactory.resumePolling()
            return true
        }

        var newFactory: WorkerFactory? = null
        return try {
            val client = WorkflowClient.newInstance(
                service,
                WorkflowClientOptions.newBuilder().setNamespace(namespace).build(),
            )
            val factory = WorkerFactory.newInstance(client)
            newFactory = factory
            factory.newWorker(taskQueue).registerActivitiesImplementations(activity)
            factory.suspendPolling()
            factory.start()
            factory.resumePolling()
            workerFactory = factory
            true
        } catch (_: Exception) {
            newFactory?.shutdownNow()
            workerFactory = null
            false
        }
    }

    @Synchronized
    private fun suspendPolling() {
        workerFactory?.suspendPolling()
    }

    override fun close() {
        gate.close()
        workerFactory?.shutdown()
        service.shutdown()
    }
}
