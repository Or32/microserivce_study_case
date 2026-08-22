package com.example.showcase.activity.customerProvisioning;

import com.example.showcase.activity.util.ActivityService;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Context
@Singleton
public class CustomerProvisioningActivityService extends ActivityService {
  public CustomerProvisioningActivityService(
      @Value("${temporal.target}") String target,
      @Value("${temporal.namespace}") String namespace,
      @Value("${temporal.task-queue}") String taskQueue,
      CustomerProvisioningActivityImpl activity) {
    startWorker(target, namespace, taskQueue, activity);
  }
}
