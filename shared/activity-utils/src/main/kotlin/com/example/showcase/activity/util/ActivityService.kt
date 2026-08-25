package com.example.showcase.activity.util

import jakarta.annotation.PreDestroy

/** Base class for a microservice that owns one Temporal activity. */
abstract class ActivityService {
    private var worker: ActivityWorker? = null

    protected fun startWorker(temporalTarget: String, temporalNamespace: String, taskQueue: String, activity: Any) {
        worker = ActivityWorker.start(temporalTarget, temporalNamespace, taskQueue, activity)
    }

    @PreDestroy
    fun stopWorker() { worker?.close() }
}
