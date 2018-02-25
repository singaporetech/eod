package com.boliao.eod;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static java.lang.Thread.sleep;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UserNameService extends IntentService {
    private static final String TAG = "UserNameService";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_STORENAME = "com.boliao.eod.action.STORENAME";
    private static final String ACTION_BAZ = "com.boliao.eod.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.boliao.eod.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.boliao.eod.extra.PARAM2";

    public UserNameService() {
        super("UserNameService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionStoreName(Context context, String username) {
        Intent intent = new Intent(context, UserNameService.class);
        intent.setAction(ACTION_STORENAME);
        intent.putExtra(EXTRA_PARAM1, username);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, UserNameService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_STORENAME.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionFoo(param1);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String usernameStr) {
        // TODO: Handle action Foo

        // do some crazy processing (imagine)
        try {
            sleep(8000);
        } catch (InterruptedException e) {
            Log.i(TAG, "Sleep interrupted");
        }

        // save username
        SharedPreferences pref = getSharedPreferences(Splash.PREF_FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();
        prefEditor.putString(usernameStr, usernameStr);
        prefEditor.commit();

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
