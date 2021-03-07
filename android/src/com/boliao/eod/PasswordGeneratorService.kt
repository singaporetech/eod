package com.boliao.eod

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import kotlinx.coroutines.*

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_ENCRYPT = "com.boliao.eod.action.ENCRYPT"

// TODO: Rename parameters
private const val EXTRA_NAME = "com.boliao.eod.extra.NAME"

/**
 * A password generator service that mocks a very CPU-intensive cryptography algo.
 * Based an auto generated [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * NOTE THAT:
 *  - IntentService is now deprecated but still available for use from the "New" menu
 *  - if need the same behavior, use JobIntentService now, although WorkManager is much more flexi
 *  - JobIntentService uses the JobScheduler API from Android O, and uses old IntentService prior
 */
class PasswordGeneratorService :
        IntentService("PasswordGeneratorService"),
        CoroutineScope by MainScope() {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_ENCRYPT -> {
                val name = intent.getStringExtra(EXTRA_NAME)

                name?.let {
                    launch(Dispatchers.IO) {
                        val eodApp = applicationContext as EODApp
                        eodApp.playerRepo.updatePw(name, getEncryptedPw(it))

                        // DEBUG to view whether db updated correctly after some time
                        delay(6000) // coroutine method
                        val players = eodApp.playerRepo.getPlayer(name)
                        Log.d(TAG, "in handleIntent just added pw = ${players[0].pw}")
                    }
                }
            }
        }
    }

    /**
     * A function that simply sleeps for 5s to mock a cpu-intensive task
     * It also does some amazing manip to the name string
     * @param name of the record to generate pw for
     * @return (pseudo-)encrypted pw String
     */
    private fun getEncryptedPw(name: String): String {
        Thread.sleep(5000)
        return name + "888888"
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionEncrypt(context: Context, name: String) {
            val intent = Intent(context, PasswordGeneratorService::class.java).apply {
                action = ACTION_ENCRYPT
                putExtra(EXTRA_NAME, name)
            }
            context.startService(intent)
        }

        private val TAG = PasswordGeneratorService::class.simpleName
    }
}