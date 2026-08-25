package com.example.showcase.activity.common

/** A business failure that must never be retried. */
class CriticalBusinessException(message: String) : RuntimeException(message)
