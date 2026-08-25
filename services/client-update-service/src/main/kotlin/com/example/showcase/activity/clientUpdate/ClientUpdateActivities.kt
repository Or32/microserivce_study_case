package com.example.showcase.activity.clientUpdate
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.util.ActivityService
import com.example.showcase.activity.util.RequestLogger
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
@Singleton class ClientUpdateActivityImpl : ClientUpdateActivity {
    private val log = RequestLogger.forClass(ClientUpdateActivityImpl::class.java)
    override fun update(context: RequestContext, status: String, detail: String?) { log.info(context, "MOCK CLIENT UPDATE: status={}, detail={}", status, detail) }
}
@Context @Singleton class ClientUpdateActivityService(@Value("\${temporal.target}") target: String, @Value("\${temporal.namespace}") namespace: String, @Value("\${temporal.task-queue}") taskQueue: String, activity: ClientUpdateActivityImpl) : ActivityService() { init { startWorker(target, namespace, taskQueue, activity) } }
