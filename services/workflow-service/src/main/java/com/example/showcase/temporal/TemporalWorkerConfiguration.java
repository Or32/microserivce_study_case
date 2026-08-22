package com.example.showcase.temporal;

import com.example.showcase.workflow.OnboardingWorkflowImpl;
import com.example.showcase.workflow.PaymentWorkflowImpl;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;

@Context
@Singleton
public class TemporalWorkerConfiguration {
  private final WorkflowServiceStubs service;
  private final WorkerFactory workerFactory;
  private final WorkflowClient workflowClient;
  private final String taskQueue;

  public TemporalWorkerConfiguration(
      @Value("${temporal.target}") String target,
      @Value("${temporal.namespace}") String namespace,
      @Value("${temporal.task-queue}") String taskQueue) {
    this.taskQueue = taskQueue;
    service =
        WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder().setTarget(target).build());
    workflowClient =
        WorkflowClient.newInstance(
            service, WorkflowClientOptions.newBuilder().setNamespace(namespace).build());
    workerFactory = WorkerFactory.newInstance(workflowClient);
    Worker worker = workerFactory.newWorker(taskQueue);
    worker.registerWorkflowImplementationTypes(
        OnboardingWorkflowImpl.class, PaymentWorkflowImpl.class);
    workerFactory.start();
  }

  public WorkflowClient workflowClient() {
    return workflowClient;
  }

  public String taskQueue() {
    return taskQueue;
  }

  @PreDestroy
  void stop() {
    workerFactory.shutdown();
    service.shutdown();
  }
}
