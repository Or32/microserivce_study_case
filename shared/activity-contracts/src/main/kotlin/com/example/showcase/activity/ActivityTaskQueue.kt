package com.example.showcase.activity

/** Declares the Temporal task queue handled by an activity contract. */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ActivityTaskQueue(val value: String)
