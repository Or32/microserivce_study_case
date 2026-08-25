package com.example.showcase.workflow

import com.example.showcase.activity.clientUpdate.ClientUpdateActivity
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.paymentCharging.PaymentChargingActivity
import com.example.showcase.activity.receipt.ReceiptActivity
import com.example.showcase.activity.validation.ValidationActivity
import com.example.showcase.api.PaymentRequest

class PaymentWorkflowImpl : PaymentWorkflow {
    private val validation = WorkflowSettings.activity(ValidationActivity::class.java)
    private val paymentCharging = WorkflowSettings.activity(PaymentChargingActivity::class.java)
    private val receipt = WorkflowSettings.activity(ReceiptActivity::class.java)
    private val clientUpdate = WorkflowSettings.activity(ClientUpdateActivity::class.java)

    override fun run(request: PaymentRequest): FlowResult {
        val requestId = requireNotNull(request.requestId)
        val context = RequestContext(requestId)
        return try {
            validation.validate(context)
            paymentCharging.charge(context, request)
            receipt.issue(context)
            clientUpdate.update(context, "SUCCESS", "Payment completed")
            FlowResult(requestId, "SUCCESS", "Payment completed")
        } catch (failure: RuntimeException) {
            clientUpdate.update(context, "FAILED", failure.message)
            FlowResult(requestId, "FAILED", failure.message)
        }
    }
}
