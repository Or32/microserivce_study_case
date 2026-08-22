package com.example.showcase.activity.receipt;

import com.example.showcase.activity.util.ActivityService;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Context
@Singleton
public class ReceiptActivityService extends ActivityService {
  public ReceiptActivityService(
      @Value("${temporal.target}") String target,
      @Value("${temporal.namespace}") String namespace,
      @Value("${temporal.task-queue}") String taskQueue,
      ReceiptActivityImpl activity) {
    startWorker(target, namespace, taskQueue, activity);
  }
}
