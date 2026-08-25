package com.example.showcase.activity.validation

import com.example.showcase.activity.ActivityTaskQueue
import com.example.showcase.activity.common.RequestContext
import io.temporal.activity.ActivityInterface

@ActivityInterface
@ActivityTaskQueue("validation-activities")
interface ValidationActivity { fun validate(context: RequestContext) }
