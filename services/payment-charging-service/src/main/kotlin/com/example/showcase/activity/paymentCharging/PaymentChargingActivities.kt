package com.example.showcase.activity.paymentCharging
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.util.ActivityService
import com.example.showcase.activity.util.RequestLogger
import com.example.showcase.api.PaymentRequest
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
@Singleton class PaymentChargingActivityImpl : PaymentChargingActivity {
    private val log = RequestLogger.forClass(PaymentChargingActivityImpl::class.java)
    override fun charge(context: RequestContext, request: PaymentRequest) { log.info(context, "Charged {} cents to {}", request.amountCents, request.customerId) }
}
@Context @Singleton class PaymentChargingActivityService(@Value("\${temporal.target}") target: String, @Value("\${temporal.namespace}") namespace: String, @Value("\${temporal.task-queue}") taskQueue: String, activity: PaymentChargingActivityImpl) : ActivityService() { init { startWorker(target, namespace, taskQueue, activity) } }
