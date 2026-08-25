package com.example.showcase.activity.welcomeEmail
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.util.ActivityService
import com.example.showcase.activity.util.RequestLogger
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
@Singleton class WelcomeEmailActivityImpl : WelcomeEmailActivity {
    private val log = RequestLogger.forClass(WelcomeEmailActivityImpl::class.java)
    override fun send(context: RequestContext, email: String) { log.info(context, "Welcome email queued for {}", email) }
}
@Context @Singleton class WelcomeEmailActivityService(@Value("\${temporal.target}") target: String, @Value("\${temporal.namespace}") namespace: String, @Value("\${temporal.task-queue}") taskQueue: String, activity: WelcomeEmailActivityImpl) : ActivityService() { init { startWorker(target, namespace, taskQueue, activity) } }
