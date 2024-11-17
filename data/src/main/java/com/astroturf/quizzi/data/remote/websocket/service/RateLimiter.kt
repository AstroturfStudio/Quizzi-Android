package com.astroturf.quizzi.data.remote.websocket.service

internal class RateLimiter(
    private val maxRequests: Int,
    private val timeWindowMs: Long
) {
    private val requestTimestamps = ArrayDeque<Long>()

    fun tryAcquire(): Boolean = synchronized(this) {
        val currentTime = System.currentTimeMillis()
        while (requestTimestamps.isNotEmpty() &&
            currentTime - requestTimestamps.first() > timeWindowMs
        ) {
            requestTimestamps.removeFirst()
        }

        if (requestTimestamps.size < maxRequests) {
            requestTimestamps.addLast(currentTime)
            return true
        }
        return false
    }
} 