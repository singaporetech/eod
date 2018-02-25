package com.boliao.eod;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mrboliao on 25/2/18.
 */

public class ReminderJobService extends JobService {
    private static final String TAG = "ReminderJobService";

    /**
     * The meat of your task.
     * - note that this is by default on the UI thread.
     * @param params
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "PLEASE CHARGE YOUR MOBILE", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "PLEASE CHARGE YOUR MOBILE");
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
