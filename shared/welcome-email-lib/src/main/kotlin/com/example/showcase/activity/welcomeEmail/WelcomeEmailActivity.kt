package com.example.showcase.activity.welcomeEmail

import com.example.showcase.activity.ActivityTaskQueue
import com.example.showcase.activity.common.RequestContext
import io.temporal.activity.ActivityInterface

@ActivityInterface
@ActivityTaskQueue("welcome-email-activities")
interface WelcomeEmailActivity { fun send(context: RequestContext, email: String) }
