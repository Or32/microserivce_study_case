package com.example.showcase.workflow

import com.example.showcase.api.PaymentRequest
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface PaymentWorkflow { @WorkflowMethod fun run(request: PaymentRequest): FlowResult }
