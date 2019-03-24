package com.boliao.eod;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * A pseudo-singleton
 * - only one class init with context, the rest use singleton
 */
public class NetworkRequestQueue {
    private static NetworkRequestQueue instance;
    private RequestQueue requestQueue;
    private static Context context;

    static synchronized NetworkRequestQueue getInstance() {
//        if (instance == null)
//            instance = new NetworkRequestQueue(context);
        return instance;
    }

    static void init(Context context) {
        if (instance == null)
            instance = new NetworkRequestQueue(context);
    }

    private NetworkRequestQueue(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // need to use getApplicationContext to prevent leaking activities/broadcastreceivers
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    <T> void add(Request<T> request) {
        getRequestQueue().add(request);
    }
}
