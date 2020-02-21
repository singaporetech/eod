package com.boliao.eod

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * A singleton to handle requests
 * - allows someone to init object with context
 */
object NetworkRequestQueue {
    private const val TAG = "NetworkRequestQueue"
    private lateinit var context: Context

    /**
     * Init func to set the context once before others use the singleton
     */
    fun setContext(context: Context) {
        this.context = context
    }

    /**
     * A Volley RequestQueue to queue up requests
     * - use lazy init (by first person to use singleton)
     */
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    /**
     * Add Request of any type T into the request queue
     */
    fun <T> add(request: Request<T>?) {
        requestQueue.add(request)
    }
}

// goto Splash to continue NETWORKING 1