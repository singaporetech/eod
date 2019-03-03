package com.boliao.eod;

import android.os.Handler;
import android.os.HandlerThread;

public class WeatherWorkerThread extends HandlerThread {
    private Handler handler;

    public WeatherWorkerThread() {
        super("WeatherWorkerThread");
    }

    public void prepareHandler() {
       handler = new Handler(getLooper());
    }

    public void postTask(Runnable r) {
        handler.post(r);
    }

    public void postTaskDelayed(Runnable r, long delayMillis) {
        handler.postDelayed(r, delayMillis);
    }
}
