package com.example.showcase.workflow

import com.example.showcase.api.OnboardingRequest
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface OnboardingWorkflow { @WorkflowMethod fun run(request: OnboardingRequest): FlowResult }
