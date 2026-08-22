package com.example.showcase.activity.welcomeEmail;

import com.example.showcase.activity.util.ActivityService;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Context
@Singleton
public class WelcomeEmailActivityService extends ActivityService {
  public WelcomeEmailActivityService(
      @Value("${temporal.target}") String target,
      @Value("${temporal.namespace}") String namespace,
      @Value("${temporal.task-queue}") String taskQueue,
      WelcomeEmailActivityImpl activity) {
    startWorker(target, namespace, taskQueue, activity);
  }
}
