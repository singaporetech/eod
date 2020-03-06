package com.boliao.eod

import android.os.Handler
import android.os.HandlerThread

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