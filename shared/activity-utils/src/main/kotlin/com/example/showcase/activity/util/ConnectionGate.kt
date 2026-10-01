package com.example.showcase.activity.util

import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

fun interface ConnectionGateCheck {
    fun isHealthy(): Boolean
}

interface ConnectionGatedActivity {
    fun connectionGateChecks(): List<ConnectionGateCheck>
}

internal class ConnectionGate(
    private val checks: List<ConnectionGateCheck>,
    private val onOpen: () -> Boolean,
    private val onClose: () -> Unit,
    private val initialRetryDelay: Duration = Duration.ofSeconds(1),
    private val maxRetryDelay: Duration = Duration.ofSeconds(30),
) : AutoCloseable {
    private val log = LoggerFactory.getLogger(ConnectionGate::class.java)
    private val scheduler = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "connection-gate").apply { isDaemon = true }
    }
    private val closed = AtomicBoolean(false)
    private var open = false
    private var nextDelayMillis = initialRetryDelay.toMillis()

    fun start() = schedule(0)

    private fun schedule(delayMillis: Long) {
        if (!closed.get()) scheduler.schedule(::evaluate, delayMillis, TimeUnit.MILLISECONDS)
    }

    private fun evaluate() {
        if (closed.get()) return

        val healthy = checks.all { check ->
            try {
                check.isHealthy()
            } catch (error: Exception) {
                log.debug("Connection-gate health check failed", error)
                false
            }
        }

        if (healthy && !open) {
            open = try {
                onOpen()
            } catch (error: Exception) {
                log.warn("Unable to open activity task polling; will retry", error)
                false
            }
            if (open) log.info("Connection gate opened; activity task polling resumed")
        } else if (!healthy && open) {
            open = false
            onClose()
            log.warn("Connection gate closed; activity task polling paused")
        }

        val delay = if (open) initialRetryDelay.toMillis() else nextDelayMillis
        nextDelayMillis = if (open) {
            initialRetryDelay.toMillis()
        } else {
            (nextDelayMillis * 2).coerceAtMost(maxRetryDelay.toMillis())
        }
        schedule(delay)
    }

    override fun close() {
        if (closed.compareAndSet(false, true)) {
            if (open) onClose()
            scheduler.shutdownNow()
        }
    }
}
