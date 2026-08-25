package com.example.showcase.api

data class OnboardingRequest(val requestId: String?, val customerEmail: String)
data class PaymentRequest(val requestId: String?, val customerId: String, val amountCents: Long)
data class StartedResponse(val workflowId: String, val runId: String)
