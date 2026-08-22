package com.example.showcase.activity.paymentCharging;

import com.example.showcase.activity.ActivityTaskQueue;
import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.api.Requests.PaymentRequest;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
@ActivityTaskQueue("payment-charging-activities")
public interface PaymentChargingActivity {
  void charge(RequestContext context, PaymentRequest request);
}
