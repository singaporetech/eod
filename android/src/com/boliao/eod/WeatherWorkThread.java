package com.boliao.eod;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by mrboliao on 14/3/18.
 */

public class WeatherWorkThread extends HandlerThread {
    private Handler handler;

    public WeatherWorkThread() {
        super("WeatherWorkerThread");
    }

    public void postTask(Runnable r) {
        handler.post(r);
    }

    public void prepareHandler() {
        handler = new Handler(getLooper());
    }
}
