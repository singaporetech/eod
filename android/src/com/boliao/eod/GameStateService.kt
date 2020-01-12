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
import com.boliao.eod.AndroidLauncher
import com.boliao.eod.GameStateService

//import androidx.app.NotificationCompat;
//import androidx.core.app.NotificationCompat;
/**
 * TODO SERVICES 5: a background service to manage game state
 * - collect sensor data and send these updates to GameState in game core component
 * - determine time to spawn bugs
 * - both a Started (collect sensor data) and Bound Service (update UI continuously)
 * - this background Service will try to persist until the app is explicitly closed
 * - Q1: when will it be killed?
 * - Q2: what happens when it is killed?
 */
class GameStateService: Service(), SensorEventListener {
    private lateinit var bgThread: Thread

    // TODO NOTIFICATIONS
    // - add var for NotificationManager
    // - add ID vars for notifications
    var notificationManager: NotificationManager? = null

    // TODO SENSORS 0: create vars to interface with hardware sensors
    private var sensorManager: SensorManager? = null
    private var stepDetector: Sensor? = null

    // TODO SERVICES 6: create GameStateBinder class that extends Binder
    // - boilerplate for Bound Service
    // - init an IBinder interface to offer a handle to this class
    inner class GameStateBinder : Binder() {
        val service: GameStateService
            get() = this@GameStateService
    }

    private val binder: IBinder = GameStateBinder()
    /**
     * TODO SERVICES 7:implement onBind to return the binder interface
     * - boilerplate for Bound Service
     * @param intent to hold any info from caller
     * @return IBinder to obtain a handle to the service class
     */
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /**
     * TODO SERVICES 8: override onCreate Service lifecycle to initialize various things
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
        val sensors = sensorManager!!.getSensorList(Sensor.TYPE_ALL)
        var sensorsStr = "available sensors:"
        for (sensor in sensors) {
            sensorsStr += "\n" + sensor.name +
                    " madeBy=" + sensor.vendor +
                    " v" + sensor.version +
                    " minDelay=" + sensor.minDelay +
                    " maxRange=" + sensor.maximumRange +
                    " power=" + sensor.power
        }
        Log.i(TAG, sensorsStr)

        // TODO SENSORS 2: get handles only for required sensors
        // - if you want to show app only if user has the sensor, then do <uses-feature> in manifest
        stepDetector = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepDetector == null) Log.e(TAG, "No step sensors on device!")

        // TODO SERVICES 9: obtain and init notification manager with a channel
        // - notification channels introduced in Android Oreo
        // - need to initialize a channel before creating actual notifications
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager!!.createNotificationChannel(NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH))
    }

    /**
     * TODO SERVICES 10: implement onStartCommand to define what the service will actually do
     * - register this class as a SensorListener (extend this Service) using sensorManager
     * - add a thread to manage spawning of bugs based on a countdown
     * - spawn bug when GameState.i().isCanNotify() && !GameState.i().isAppActive()
     * - create pending intent to launch AndroidLauncher
     * - use NotificationCompat.Builder to make notification
     * @param intent to hold any info from caller
     * @param flags to show more data about how this was started (e.g., REDELIVERY)
     * @param startId id of this started instance
     * @return an int that controls what happens when this service is auto killed
     * , e.g., sticky or not (see https://goo.gl/shXLoy)
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // TODO SENSORS 3: Registering listener to listen for sensor events.
        // - note that the DELAY is the max, and system normally lower
        // - don't just use SENSOR_DELAY_FASTEST (0us) as it uses max power
        sensorManager!!.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_GAME)

        // TODO THREADING 0: control the spawn timer in a thread
        // O.M.G. a raw java thread
        bgThread = Thread( Runnable{
                try {
                    while (true) { // decrement countdown every sec
                        Thread.sleep(1000)
                        GameState.i().decTimer()
                        // notify user when bug is spawning
                        if (GameState.i().isCanNotify && !GameState.i().isAppActive) {
                            Log.i(TAG, "The NIGHT has come: a bug will spawn...")

                            // TODO SERVICES 11: create pending intent to open app from notification
                            val intent2 = Intent(this@GameStateService, AndroidLauncher::class.java)
                            val pi = PendingIntent.getActivity(this@GameStateService, PENDINGINTENT_ID, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
                            // build the notification
                            val noti = Notification.Builder(this@GameStateService, NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setContentTitle("Exercise Or Die")
                                    .setColor(Color.RED)
                                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                                    .setContentText("OMG NIGHT TIME lai liao, BUGs will spawn")
                                    .setAutoCancel(true)
                                    .setContentIntent(pi)
                                    .build()
                            // activate the notification
                            notificationManager!!.notify(NOTIFY_ID, noti)

                            // TODO SERVICES 12: upgrade this service to foreground
                            // - need to startForegroundService from caller context
                            // - activate the ongoing notification using startForeground (needs to be called within 5s of above
                            // - move the notification to become a one time and change the premise
                            // startForeground(NOTIFY_ID, noti);
                        }
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
        })
        bgThread.start()

        // TODO SERVICE 13: return appropriate flag to indicate what happens when killed
        return START_STICKY
    }

    /**
     * TODO SERVICE 14: override Service's onDestroy to destroy any background activity if desired
     * - also destroy any manual threads
     */
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)

        // TODO SENSORS 4: unregister listeners from the sensorManager as appropriate
        sensorManager!!.unregisterListener(this, stepDetector)

        // TODO THREADING 0: "stop" raw threads?
        // here's an example of the iffiniess of using raw threads: no good way to stop it
        // bgThread.stop(); // has been deprecated
        // bgThread.interrupt();

        // TODO THREADING n: go to Splash
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
                sendBroadcast(GameState.i().steps)
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

    /**
     * TODO BROADCASTRECEIVERS 2: Send broadcast to apps that wish to get step count
     * TODO BROADCASTRECEIVERS 3: Receive broadcast in another separate app
     */
    private fun sendBroadcast(steps: Int) {
        val intent = Intent(BROADCAST_ACTION)
        intent.putExtra(STEP_KEY, steps)
        Log.i(TAG, "Sending broadcast steps=$steps")
        sendBroadcast(intent)
    }

    companion object {
        private val TAG = GameStateService::class.simpleName
        const val BROADCAST_ACTION = "com.boliao.eod.STEP_COUNT"
        const val STEP_KEY = "com.boliao.eod.STEP_KEY"
        private const val NOTIFICATION_CHANNEL_ID = "EOD CHANNEL"
        private const val NOTIFY_ID = 888
        private const val PENDINGINTENT_ID = 1
    }
}