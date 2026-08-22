package com.example.showcase.workflow;

import com.example.showcase.activity.clientUpdate.ClientUpdateActivity;
import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.paymentCharging.PaymentChargingActivity;
import com.example.showcase.activity.receipt.ReceiptActivity;
import com.example.showcase.activity.validation.ValidationActivity;
import com.example.showcase.api.Requests.PaymentRequest;

public class PaymentWorkflowImpl implements PaymentWorkflow {
  private final ValidationActivity validation = WorkflowSettings.activity(ValidationActivity.class);
  private final PaymentChargingActivity paymentCharging = WorkflowSettings.activity(PaymentChargingActivity.class);
  private final ReceiptActivity receipt = WorkflowSettings.activity(ReceiptActivity.class);
  private final ClientUpdateActivity clientUpdate = WorkflowSettings.activity(ClientUpdateActivity.class);

  @Override
  public FlowResult run(PaymentRequest request) {
    RequestContext context = new RequestContext(request.requestId());
    try {
      validation.validate(context);
      paymentCharging.charge(context, request);
      receipt.issue(context);
      clientUpdate.update(context, "SUCCESS", "Payment completed");
      return new FlowResult(request.requestId(), "SUCCESS", "Payment completed");
    } catch (RuntimeException failure) {
      clientUpdate.update(context, "FAILED", failure.getMessage());
      return new FlowResult(request.requestId(), "FAILED", failure.getMessage());
    }
  }
}
