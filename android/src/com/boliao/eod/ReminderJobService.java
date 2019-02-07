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
     * @return bool to tell the system whether this job is done at the end of this method
     *         e.g., if you offloaded this to a thread, then return true
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        Toast.makeText(this, "PLEASE CHARGE YOUR MOBILE", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "PLEASE CHARGE YOUR MOBILE");
        jobFinished(params, false);

        return false;
    }

    /**
     *
     * @param params
     * @return whether job needs to be restarted based on backoff policy
     *         may need to handle when job got stalled (e.g., no wifi) and not ended yet, and
     *         this is called. If so, cancel the thread yourself and let system restart job.
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
