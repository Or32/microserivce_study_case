package com.example.showcase.workflow

import com.example.showcase.activity.ActivityTaskQueue
import com.example.showcase.activity.common.CriticalBusinessException
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Workflow
import java.time.Duration

internal object WorkflowSettings {
    private fun options(taskQueue: String): ActivityOptions = ActivityOptions.newBuilder()
        .setTaskQueue(taskQueue)
        .setStartToCloseTimeout(Duration.ofSeconds(20))
        .setRetryOptions(
            RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setBackoffCoefficient(2.0)
                .setMaximumAttempts(3)
                .setDoNotRetry(CriticalBusinessException::class.java.name)
                .build(),
        ).build()

    fun <T> activity(activityType: Class<T>): T {
        val taskQueue = activityType.getAnnotation(ActivityTaskQueue::class.java)
            ?: throw IllegalArgumentException("Activity contract ${activityType.name} must declare @ActivityTaskQueue")
        return Workflow.newActivityStub(activityType, options(taskQueue.value))
    }
}
