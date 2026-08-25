package com.example.showcase.activity.receipt
import com.example.showcase.activity.common.RequestContext
import com.example.showcase.activity.util.ActivityService
import com.example.showcase.activity.util.RequestLogger
import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
@Singleton class ReceiptActivityImpl : ReceiptActivity {
    private val log = RequestLogger.forClass(ReceiptActivityImpl::class.java)
    override fun issue(context: RequestContext) { log.info(context, "Receipt issued") }
}
@Context @Singleton class ReceiptActivityService(@Value("\${temporal.target}") target: String, @Value("\${temporal.namespace}") namespace: String, @Value("\${temporal.task-queue}") taskQueue: String, activity: ReceiptActivityImpl) : ActivityService() { init { startWorker(target, namespace, taskQueue, activity) } }
