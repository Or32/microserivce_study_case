package com.example.showcase.activity.clientUpdate;

import com.example.showcase.activity.util.ActivityService;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Context
@Singleton
public class ClientUpdateActivityService extends ActivityService {
  public ClientUpdateActivityService(
      @Value("${temporal.target}") String target,
      @Value("${temporal.namespace}") String namespace,
      @Value("${temporal.task-queue}") String taskQueue,
      ClientUpdateActivityImpl activity) {
    startWorker(target, namespace, taskQueue, activity);
  }
}
