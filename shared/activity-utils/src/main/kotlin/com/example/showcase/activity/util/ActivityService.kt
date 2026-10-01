package com.example.showcase.activity.util

import jakarta.annotation.PreDestroy

abstract class ActivityService {
    private var worker: ActivityWorker? = null

    protected fun startWorker(
        temporalTarget: String,
        temporalNamespace: String,
        taskQueue: String,
        activity: Any,
        checks: List<ConnectionGateCheck> = emptyList(),
    ) {
        worker = ActivityWorker.start(temporalTarget, temporalNamespace, taskQueue, activity, checks)
    }

    @PreDestroy
    fun stopWorker() { worker?.close() }
}
