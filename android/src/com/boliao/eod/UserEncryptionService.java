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
public class UserEncryptionService extends IntentService {
    private static final String TAG = "UserEncryptionService";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_ENCRYPT = "com.boliao.eod.action.ENCRYPT";
    private static final String ACTION_BAZ = "com.boliao.eod.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.boliao.eod.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.boliao.eod.extra.PARAM2";

    public UserEncryptionService() {
        super("UserEncryptionService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionEncrypt(Context context, String userNameStr) {
        Intent intent = new Intent(context, UserEncryptionService.class);
        intent.setAction(ACTION_ENCRYPT);
        intent.putExtra(EXTRA_PARAM1, userNameStr);
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
        Intent intent = new Intent(context, UserEncryptionService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ENCRYPT.equals(action)) {
                final String userNameStr = intent.getStringExtra(EXTRA_PARAM1);
                handleActionEncrypt(userNameStr);
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
    private void handleActionEncrypt(String usernameStr) {
        try {
            sleep(5000);
        } catch (Exception e) {
            Log.i(TAG, "pls catch this lah...");
        }
        // store the username
        SharedPreferences prefs = getSharedPreferences(Splash.PREF_FILENAME, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
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
