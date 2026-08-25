package com.example.showcase.activity.util

import com.example.showcase.activity.common.RequestContext
import io.temporal.activity.Activity
import org.slf4j.LoggerFactory
import org.slf4j.MDC

/** Adds the fields needed to follow one Temporal activity to every log message. */
class RequestLogger private constructor(source: Class<*>) {
    private val logger = LoggerFactory.getLogger(source)
    private val activity = source.simpleName.removeSuffix("Impl")

    companion object { fun forClass(source: Class<*>) = RequestLogger(source) }

    fun info(context: RequestContext, message: String, vararg arguments: Any?) =
        withContext(context) { logger.info(message, *arguments) }

    fun warn(context: RequestContext, message: String, vararg arguments: Any?) =
        withContext(context) { logger.warn(message, *arguments) }

    private fun withContext(context: RequestContext, action: () -> Unit) {
        MDC.putCloseable("requestId", context.requestId).use {
            MDC.putCloseable("retry", retryNumber()).use {
                MDC.putCloseable("activity", activity).use { action() }
            }
        }
    }

    private fun retryNumber() = try {
        Activity.getExecutionContext().info.attempt.toString()
    } catch (_: IllegalStateException) {
        "n/a"
    }
}
