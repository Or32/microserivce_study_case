package com.example.showcase.activity.paymentCharging

import com.example.showcase.activity.ActivityTaskQueue
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.api.PaymentRequest
import io.temporal.activity.ActivityInterface

@ActivityInterface
@ActivityTaskQueue("payment-charging-activities")
interface PaymentChargingActivity { fun charge(context: RequestContext, request: PaymentRequest) }
