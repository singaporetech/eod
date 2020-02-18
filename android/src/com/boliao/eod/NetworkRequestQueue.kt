package com.boliao.eod

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * A pseudo-singleton
 * - only one class init with context, the rest use singleton
 */
object NetworkRequestQueue {
    private const val TAG = "NetworkRequestQueue"
    private lateinit var context: Context

    /**
     * Init func to set the context once before the rest use the singleton
     */
    fun setContext(context: Context) {
        this.context = context
    }

    /**
     * Store a Volley RequestQueue by lazy init
     */
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    /**
     * Add any type<T> of request queue
     */
    fun <T> add(request: Request<T>?) {
        requestQueue.add(request)
    }
}
