package com.boliao.eod

import android.os.Handler
import android.os.HandlerThread

/**
 * A HandlerThread for weather runnables (jobs) to land on.
 * - has a Looper that constantly conveys Runnables to be acted on
 * - has a Handler to post Runnables onto the Looper
 *   Handler can postDelayed Runnables
 */
class WeatherWorkerThread : HandlerThread("WeatherWorkerThread") {
    private lateinit var handler: Handler

    // need this as looper only available after initializing HandlerThread
    fun prepareHandler() {
        handler = Handler(looper)
    }

    fun postTask(r: Runnable) {
        handler.post(r)
    }

    fun postTaskDelayed(r: Runnable, delayMillis: Long) {
        handler.postDelayed(r, delayMillis)
    }
}