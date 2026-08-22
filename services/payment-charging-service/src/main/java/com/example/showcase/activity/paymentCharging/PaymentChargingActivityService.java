package com.example.showcase.activity.paymentCharging;

import com.example.showcase.activity.util.ActivityService;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Context
@Singleton
public class PaymentChargingActivityService extends ActivityService {
  public PaymentChargingActivityService(
      @Value("${temporal.target}") String target,
      @Value("${temporal.namespace}") String namespace,
      @Value("${temporal.task-queue}") String taskQueue,
      PaymentChargingActivityImpl activity) {
    startWorker(target, namespace, taskQueue, activity);
  }
}
