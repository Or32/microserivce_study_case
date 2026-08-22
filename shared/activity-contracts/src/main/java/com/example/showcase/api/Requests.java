package com.example.showcase.api;

public final class Requests {
  private Requests() {}

  public record OnboardingRequest(String requestId, String customerEmail) {}

  public record PaymentRequest(String requestId, String customerId, long amountCents) {}

  public record StartedResponse(String workflowId, String runId) {}
}
