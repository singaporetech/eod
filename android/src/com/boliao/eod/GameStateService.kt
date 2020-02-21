package com.boliao.eod

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log

/**
 * TODO SERVICES 3: a background service to manage game state
 * This is like the background engine for the app. Everything should be done in the background
 * (even when app not visible)
 * This is an e.g. of both a Started (collect sensor data) and Bound Service (update UI continuously)
 * - track and update steps using sensor listeners
 * - send updates to GameState in com.boliao.eod.core
 * - manage countdown timer to spawn bugs
 * - check if service already be running from a previous launch
 * - persist this Service until the app is explicitly closed
 *
 * Q1: when will it be killed?
 * Q2: what happens when it is killed?
 */
class GameStateService: Service(), SensorEventListener {
    companion object {
        private val TAG = GameStateService::class.simpleName
        private const val NOTIFICATION_CHANNEL_ID = "EOD CHANNEL"
        private const val NOTIFY_ID = 888
        private const val PENDINGINTENT_ID = 1
    }

    // a raw thread for bg work
    private lateinit var bgThread: Thread

    // TODO SERVICES 10: declare vars for NOTIFICATIONS
    // - add ID vars for notifications in companion object above
    // - add var for NotificationManager

    // TODO SENSORS 0: create vars to interface with hardware sensors
    private lateinit var sensorManager: SensorManager
    private var stepDetector: Sensor? = null

    /**
     * TODO SERVICES 4: create GameStateBinder class to "contain" this service
     * This is part of the boilerplate for Bound Service. Client can use this object to communicate
     * with the service. This approach uses the simple Binder class since clients are also in this
     * app/process. For this service to be used by other apps, use Messenger or AIDL for IPC.
     * - extends Binder()
     * - init an IBinder interface to offer a handle to this class
     * - return this service for clients to access public service methods
     */
    inner class GameStateBinder : Binder() {
        fun getService(): GameStateService = this@GameStateService
    }
    private val binder = GameStateBinder()

    /**
     * TODO SERVICES 5:implement onBind to return the binder interface
     * Part of the boilerplate for Bound Service
     * @param intent to hold any info from caller
     * @return IBinder to obtain a handle to the service class
     */
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /**
     * TODO SERVICES 6: override onCreate Service lifecycle to initialize various things
     * - get handle to SensorManager from a System Service
     * - get list of available sensors from the sensorManager
     * - get handle to step detector from sensorManager
     * - init NotificationManager and NotificationChannel
     */
    override fun onCreate() {
        super.onCreate()

        // TODO SENSORS 1: get handle to sensor device and list all sensors
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // get list of all available sensors, along with some capability data
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
        var sensorsStr = "available sensors:"
        for (sensor in sensors) {
            sensorsStr += "\n$sensor.name madeBy=$sensor.vendor v=$sensor.version " +
                    "minDelay=$sensor.minDelay maxRange=$sensor.maximumRange power=$sensor.power"
        }
        Log.i(TAG, sensorsStr)

        // TODO SENSORS 2: get handles only for required sensors
        // - if you want to show app only if user has the sensor, then do <uses-feature> in manifest
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepDetector == null) Log.e(TAG, "No step sensors on device!")

        // TODO SERVICES 9: obtain and init notification manager with a channel
        // - get notification manager from system service using the NOTIFICATION_SERVICE code
        // - create notification channel if build version SDK_INT above build version code Oreo
        //   (need to initialize a channel before creating actual notifications)
        // - set the channel to high importance
    }

    /**
     * TODO SERVICES 7: implement onStartCommand to define what the service will actually do
     * - register this class as a SensorListener (extend this Service) using sensorManager
     * - add a thread to manage spawning of bugs based on a countdown
     * - spawn bug when GameState.i().isCanNotify() && !GameState.i().isAppActive()
     * - create a notification when spawn occurs
     * @param intent to hold any info from caller
     * @param flags to show more data about how this was started (e.g., REDELIVERY)
     * @param startId id of this started instance
     * @return an int that controls what happens when this service is auto killed
     *         e.g., sticky or not (see https://goo.gl/shXLoy)
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // TODO SENSORS 3: Registering listener to listen for sensor events.
        // - note that the DELAY is the max, and system normally lower
        // - don't just use SENSOR_DELAY_FASTEST (0us) as it uses max power
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_GAME)

        // TODO THREADING 0: control the spawn timer in a thread
        // O.M.G. a raw java thread
        bgThread = Thread( Runnable{
                try {
                    while (true) {
                        // thread updates every sec
                        Thread.sleep(1000)

                        // decrement countdown every sec
                        GameState.i().decTimer()

                        // notify user when bug is spawning
                        if (GameState.i().isCanNotify && !GameState.i().isAppActive) {
                            Log.i(TAG, "The NIGHT has come: a bug will spawn...")

                            // TODO SERVICES 11: create pending intent to open app from notification
                            // - create a intent from this GameStateService context that launches AndroidLauncher

                            // - wrap the intent into a pending intent for triggering in future

                            // - build the notification with small icon R.drawable.ic_stat_name,
                            //   a content title and some content text, some color,
                            //   visibility to public, can be autocancelled, content intent to pi

                            // - use manager to trigger notify with the NOTIFY_ID and the
                            //   notification set up above

                            // TODO SERVICES 12: upgrade this service to foreground
                            // - change to startForegroundService (from startService) from caller context
                            // - move the notification out of thread into onCreate
                            // - activate the ongoing notification using startForeground
                            //   (needs to be called within 5s of above)
                        }
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
        })

        // get the thread going
        bgThread.start()

        // TODO SERVICES 8: return appropriate flag to indicate what happens when killed
        // Q: what are the other flags?
        return START_STICKY
    }

    /**
     * TODO SERVICES 9: override Service's onDestroy to destroy any background activity if desired
     * - also destroy any manual threads
     */
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)

        // TODO SENSORS 4: unregister listeners from the sensorManager as appropriate
        sensorManager.unregisterListener(this, stepDetector)

        // TODO THREADING 0: "stop" raw threads?
        // here's an example of the iffiniess of using raw threads: no good way to stop it
        // bgThread.stop(); // has been deprecated
        // bgThread.interrupt();

        // goto Splash for THREADING 1
    }

    /**
     * TODO SENSORS 5: implement onSensorChanged callback
     * - system will call this back when sensor has new vals
     * - simply call GameState.i().incSteps(event.values[0])
     * if event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR
     * - log value for debugging
     * - do as minimal as possible (this is called VERY frequently)
     *
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (event.values.size > 0) {
            val `val` = event.values[0].toInt()
            // update game state based on sensor vals
            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                Log.d(TAG, "Step detector:$`val`")
                GameState.i().incSteps(`val`)
            }
        }
    }

    /**
     * TODO SENSORS 6: implement onAccuracyChanged callback
     * - system will call this back when sensor accuracy changed
     * - just show a log msg here but may want to only track steps on HIGH  ACCURACY
     */
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.i(TAG, "Sensor accuracy changed to $accuracy")
    }
}