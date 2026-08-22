package com.example.showcase.activity.util;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

/** Starts one Temporal worker for one activity implementation. */
public final class ActivityWorker implements AutoCloseable {
  private final WorkflowServiceStubs service;
  private final WorkerFactory workerFactory;

  private ActivityWorker(WorkflowServiceStubs service, WorkerFactory workerFactory) {
    this.service = service;
    this.workerFactory = workerFactory;
  }

  public static ActivityWorker start(
      String target, String namespace, String taskQueue, Object activity) {
    WorkflowServiceStubs service =
        WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder().setTarget(target).build());
    WorkflowClient client =
        WorkflowClient.newInstance(
            service, WorkflowClientOptions.newBuilder().setNamespace(namespace).build());
    WorkerFactory factory = WorkerFactory.newInstance(client);
    Worker worker = factory.newWorker(taskQueue);
    worker.registerActivitiesImplementations(activity);
    factory.start();
    return new ActivityWorker(service, factory);
  }

  @Override
  public void close() {
    workerFactory.shutdown();
    service.shutdown();
  }
}
