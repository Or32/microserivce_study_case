package com.example.showcase.activity.clientUpdate

import com.example.showcase.activity.ActivityTaskQueue
import com.example.showcase.activity.common.RequestContext
import io.temporal.activity.ActivityInterface

@ActivityInterface
@ActivityTaskQueue("client-update-activities")
interface ClientUpdateActivity { fun update(context: RequestContext, status: String, detail: String?) }
