package com.example.showcase.workflow;

import com.example.showcase.activity.ActivityTaskQueue;
import com.example.showcase.activity.common.CriticalBusinessException;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

final class WorkflowSettings {
  private WorkflowSettings() {}

  private static ActivityOptions options(String taskQueue) {
    RetryOptions retry =
        RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1))
            .setBackoffCoefficient(2)
            .setMaximumAttempts(3)
            .setDoNotRetry(CriticalBusinessException.class.getName())
            .build();
    return ActivityOptions.newBuilder()
        .setTaskQueue(taskQueue)
        .setStartToCloseTimeout(Duration.ofSeconds(20))
        .setRetryOptions(retry)
        .build();
  }

  static <T> T activity(Class<T> activityType) {
    ActivityTaskQueue taskQueue = activityType.getAnnotation(ActivityTaskQueue.class);
    if (taskQueue == null) {
      throw new IllegalArgumentException(
          "Activity contract " + activityType.getName() + " must declare @ActivityTaskQueue");
    }
    return Workflow.newActivityStub(activityType, options(taskQueue.value()));
  }
}
