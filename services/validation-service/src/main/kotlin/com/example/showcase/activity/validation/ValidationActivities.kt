package com.example.showcase.activity.validation
import com.example.showcase.activity.common.CriticalBusinessException
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.util.ActivityService
import com.example.showcase.activity.util.RequestLogger
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import io.temporal.failure.ApplicationFailure
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
@Singleton class ValidationActivityImpl : ValidationActivity {
    private val log = RequestLogger.forClass(ValidationActivityImpl::class.java)
    private val attempts = ConcurrentHashMap<String, AtomicInteger>()
    override fun validate(context: RequestContext) {
        val requestId = context.requestId
        if (requestId.startsWith("critical-")) throw ApplicationFailure.newNonRetryableFailure("Request violates a critical business rule", CriticalBusinessException::class.java.name)
        val attempt = attempts.computeIfAbsent(requestId) { AtomicInteger() }.incrementAndGet()
        if (requestId.startsWith("retry-") && attempt == 1) {
            log.warn(context, "Simulating a transient validation failure; Temporal will retry it")
            throw IllegalStateException("Temporary downstream outage")
        }
        log.info(context, "Shared validation completed")
    }
}
@Context @Singleton class ValidationActivityService(@Value("\${temporal.target}") target: String, @Value("\${temporal.namespace}") namespace: String, @Value("\${temporal.task-queue}") taskQueue: String, activity: ValidationActivityImpl) : ActivityService() { init { startWorker(target, namespace, taskQueue, activity) } }
