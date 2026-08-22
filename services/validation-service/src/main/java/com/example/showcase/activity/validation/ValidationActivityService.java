package com.example.showcase.activity.validation;

import com.example.showcase.activity.util.ActivityService;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Context
@Singleton
public class ValidationActivityService extends ActivityService {
  public ValidationActivityService(
      @Value("${temporal.target}") String target,
      @Value("${temporal.namespace}") String namespace,
      @Value("${temporal.task-queue}") String taskQueue,
      ValidationActivityImpl activity) {
    startWorker(target, namespace, taskQueue, activity);
  }
}
