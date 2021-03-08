package com.boliao.eod

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * A singleton to handle requests
 * - allows someone to init object with context
 */
class NetworkRequestQueue constructor(context: Context) {
    companion object {
        private val TAG = NetworkRequestQueue::class.simpleName

        // Getting the singleton instance
        @Volatile
        private var INSTANCE: NetworkRequestQueue? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: NetworkRequestQueue(context).also {
                INSTANCE = it
            }
        }
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