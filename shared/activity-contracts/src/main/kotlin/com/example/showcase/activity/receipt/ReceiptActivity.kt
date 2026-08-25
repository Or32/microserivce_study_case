package com.example.showcase.activity.receipt

import com.example.showcase.activity.ActivityTaskQueue
import com.example.showcase.activity.common.RequestContext
import io.temporal.activity.ActivityInterface

@ActivityInterface
@ActivityTaskQueue("receipt-activities")
interface ReceiptActivity { fun issue(context: RequestContext) }
