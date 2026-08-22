package com.example.showcase.activity.paymentCharging;

import com.example.showcase.activity.common.RequestContext;
import com.example.showcase.activity.util.RequestLogger;
import com.example.showcase.api.Requests.PaymentRequest;
import jakarta.inject.Singleton;

@Singleton
public class PaymentChargingActivityImpl implements PaymentChargingActivity {
  private static final RequestLogger LOG =
      RequestLogger.forClass(PaymentChargingActivityImpl.class);

  @Override
  public void charge(RequestContext context, PaymentRequest request) {
    LOG.info(context, "Charged {} cents to {}", request.amountCents(), request.customerId());
  }
}
