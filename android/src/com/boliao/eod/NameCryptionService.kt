package com.boliao.eod

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class NameCryptionService : IntentService("NameCryptionService") {
    var pref: SharedPreferences? = null
    var prefEditor: SharedPreferences.Editor? = null
    override fun onHandleIntent(intent: Intent) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_FOO == action) {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                handleActionFoo(param1)
            } else if (ACTION_BAZ == action) {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionBaz(param1, param2)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String) { // encrypt username using some ultra modern 5s technique
        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            Log.e(TAG, "Error occured whilst eating snake: " + e.message)
        }
        // store in sharedprefs after "encryption"
        pref = getSharedPreferences(Splash.PREF_FILENAME, Context.MODE_PRIVATE)
        prefEditor = pref!!.edit()
        prefEditor!!.putString(param1, param1)
        prefEditor!!.commit()
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionBaz(param1: String, param2: String) { // TODO: Handle action Baz
        throw UnsupportedOperationException("Not yet implemented")
    }

    companion object {
        private const val TAG = "NameCryptionService"
        // TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
        private const val ACTION_FOO = "com.boliao.eod.action.FOO"
        private const val ACTION_BAZ = "com.boliao.eod.action.BAZ"
        // TODO: Rename parameters
        private const val EXTRA_PARAM1 = "com.boliao.eod.extra.PARAM1"
        private const val EXTRA_PARAM2 = "com.boliao.eod.extra.PARAM2"
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
// TODO: Customize helper method
        fun startActionFoo(context: Context, param1: String?) {
            val intent = Intent(context, NameCryptionService::class.java)
            intent.action = ACTION_FOO
            intent.putExtra(EXTRA_PARAM1, param1)
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
// TODO: Customize helper method
        fun startActionBaz(context: Context, param1: String?, param2: String?) {
            val intent = Intent(context, NameCryptionService::class.java)
            intent.action = ACTION_BAZ
            intent.putExtra(EXTRA_PARAM1, param1)
            intent.putExtra(EXTRA_PARAM2, param2)
            context.startService(intent)
        }
    }
}