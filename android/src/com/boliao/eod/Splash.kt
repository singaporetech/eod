/**
 * # WEEK11: NDK
 * Communicating data across kotlin and C/C++ code.
 *
 * 0. Review networking code to fetch online weather data
 * 1. Add NDK development capabilities to existing project
 * 2. Interfacing with a native C lib - OpenCV
 * 3. View some examples of ARCore native lib in action
 */

package com.boliao.eod

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * This is the splash view that records who is playing.
 *
 * TODO NDK 3.2: implement the CameraBridgeViewBase.CvCameraViewListener2 interface
 * This will allow the Activity to have a cam view.
 */
class Splash :
        AppCompatActivity(),
        CoroutineScope by MainScope(),
        CameraBridgeViewBase.CvCameraViewListener2 {

    private lateinit var startAndroidLauncher: Intent

    private external fun getNativeString(): String
    private external fun convertToGrayscale(rgbaAddrInput: Long, rbgaAddrResult: Long): Int
    private external fun detectFace(cascadePath: String?, rgbaAddrInput: Long)

    // TODO NDK 0: install required dependencies (from Android Studio SDK Tools)
    // - NDK: Android toolset to communicate with native code
    // - CMake: native build tool
    // - LLDB: native code debugger

    // TODO NDK 1: create the native code and build configuration
    // - create a src/cpp directory
    // - create a new .cpp file
    // - create a CMake build script called CMakeLists.txt (https://developer.android.com/studio/projects/configure-cmake.html)
    // - check that the .cpp path (relative to script) is correct in the CMake script
    // - make sure you add_library for your own libs and find_library for Android NDP native libs
    // - then link the libs together using target_link_libraries
    // - add CMake path in gradle (you can right click on folder and let IDE do it)
    // - may need to restart project afer configuration

    // TODO NDK 2: create a test method in native to receive a string and show it
    // - declare a native function you want to write in C/C++
    // - write the method in a cpp file
    // - load native lib and declare native methods
    // - paste a native function in the cpp file, and use IDE helper to fill in method name
    // - receive string from native function and display it in a toast
    // - try and debug within native using <android/log.h>

    // TODO NDK 3: use OpenCV C lib to do face recognition
    // - download OpenCV-android-sdk in a separate folder out of the project
    // - add OpenCV-android-sdk as a module
    // - check it is included in settings.gradle
    // - add as an implementation under module Project:eod's build.gradle

    // TODO NDK 3: use OpenCV C lib to do face recognition
    lateinit var camView: CameraBridgeViewBase
    lateinit var rgbaT: Mat
    lateinit var rgbaF: Mat
    lateinit var rgbaInput: Mat
    lateinit var rgbaOutput: Mat

    // TODO NDK 3: set the face model
    private lateinit var cascadeFile: File
    private val cascadeFileName = "haarcascade_eye_tree_eyeglasses.xml"

    private fun launchGame() {
        startActivity(startAndroidLauncher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // ask user for camera permissions if not granted already
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 1)
        }

        // TODO NDK 3: setup camera ui
        camView = findViewById(R.id.camview)
        camView.setCameraPermissionGranted()
        camView.visibility = SurfaceView.VISIBLE
        camView.setCvCameraViewListener(this)
        // camView.rotation = 180F
        Log.i(TAG, "Cam width = ${camView.measuredWidth} and ${camView.measuredHeight}")

        // init launch game intent
        startAndroidLauncher = Intent(this@Splash, AndroidLauncher::class.java)

        // get refs to UI components
        val playBtn = findViewById<Button>(R.id.play_btn)
        val usernameEdtTxt = findViewById<EditText>(R.id.name_edtxt)
        val msgTxtView = findViewById<TextView>(R.id.msg_txtview)
        val weatherTxtView = findViewById<TextView>(R.id.weather_txtview)

        // show splash text
        msgTxtView.setText(R.string.welcome_note)

        // TODO THREADING 2: create a persistent weather widget
        // An MVVM Splash ViewModel is already set up.
        // Splash Activity View -> Splash ViewModel -> WeatherRepo Model
        // WeatherRepo currently has a mock stub to return static mock data, provided live by
        // weatherData in SplashViewModel.
        // - set up weatherTextView here to observe the weatherData
        // - goto WeatherRepo for THREADING 3
        // Q: Do I (Splash Activity) need to know about WeatherRepo?

        // TODO NETWORKING 1: init the network request queue singleton object (volley)
        // - goto NETWORKING 0 in manifest
        // - create NetWorkRequestQueue singleton
        // - set NetworkRequestQueue's context to this
        // - goto NETWORKING 2 in WeatherRepo
        NetworkRequestQueue.setContext(this)

        // val splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        // val splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        val splashViewModel: SplashViewModel by viewModels()
        splashViewModel.weatherData.observe(this, Observer {
            weatherTxtView.text = it
        })

        splashViewModel.loginStatus.observe(this, Observer {
            if (it) {
                msgTxtView.text = "LOGIN DONE. Starting..."
                launchGame()
            } else {
                msgTxtView.text = "Name OREDI exist liao..."
            }
        })

        // start game on click "PLAY"
        playBtn.setOnClickListener {
            msgTxtView.text = "Encrypting in coroutine heaven..."
            splashViewModel.login(usernameEdtTxt.text.toString())
        }

        // provide a way to stop the service
        findViewById<Button>(R.id.exit_btn).setOnClickListener {
            AndroidLauncher.startServiceIntent?.let {
                stopService(it)
            }
            finish()
        }

        // TODO NDK 2: show the string from native
        Toast.makeText(this, getNativeString(), Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "Splash"

        init {
            System.loadLibrary("core-lib")
        }

        /**
         * [DEPRECATED] AsyncTask to "encrypt" username
         * - heavy lifting in the background to be posted back to UI
         * - static class so as to prevent leaks
         * - internal ctor to only allow enclosing class to construct
         * - need a ref to update UI thread, so use WeakReference (a.k.a. shared_ptr)
         * - onProgressUpdate(Integer... progress) left as an exercise
         * - note: publishProgress(Integer) is in built to pass progress to above from doInBackground
         */
        /*
        private class EncryptTask internal constructor(act: Splash) : AsyncTask<String?, Void?, Boolean>() {
            // hold the Activity to get all the UI elements
            // - use weak reference so that it does not leak mem when activity gets killed
            var wr_splash: WeakReference<Splash> = WeakReference(act)

            override fun onPreExecute() {
                super.onPreExecute()
                val splash = wr_splash.get()
                if (splash != null) {
                    (splash.findViewById<View>(R.id.msg_txtview) as TextView).text = "encrypting"
                }
            }

            override fun doInBackground(vararg str: String?): Boolean {
                try {
                    Thread.sleep(3000)
                    // do something to the str
                } catch (e: InterruptedException) {
                    return false
                }
                return true
            }

            override fun onPostExecute(b: Boolean) {
                super.onPostExecute(b)
                val splash = wr_splash.get()
                if (splash != null) {
                    (splash.findViewById<View>(R.id.msg_txtview) as TextView).text = "The encryption is:$b"
                    splash.launchGame()
                }
            }
        }
         */
    }

    /**
     * TODO NDK 3.3: implement cam callbacks
     * - init matrices on cam view start
     */
    override fun onCameraViewStarted(width: Int, height: Int) {
        rgbaInput = Mat(height, width, CvType.CV_8UC4)
        rgbaOutput = Mat(height, width, CvType.CV_8UC4)
        rgbaF = Mat(height, width, CvType.CV_8UC4)
        rgbaT = Mat(width, width, CvType.CV_8UC4)
    }

    /**
     * TODO NDK 3.4: implement cam callbacks
     * - release matrices on cam view stop
     */
    override fun onCameraViewStopped() {
        rgbaInput.release()
        rgbaOutput.release()
        rgbaF.release()
        rgbaT.release()
    }

    /**
     * TODO NDK 3.5: implement cam callbacks
     * - detect face and demarcate on the image to draw every frame
     */
    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        rgbaInput = inputFrame.rgba()
//        Log.i(TAG, "onCameraFrame rgbaInput is $rgbaInput")
        // do convert to grayscale
        // convertToGrayscale(rgbaInput.getNativeObjAddr(), rgbaOutput.getNativeObjAddr());

        // do face detection
        // - pass the object by reference so C++ can edit the same object
        detectFace(cascadeFile.absolutePath, rgbaInput.nativeObjAddr)
        return rgbaInput
    }

    /**
     * TODO NDK 3: load model file
     */
    private fun loadCascadeFile() {
        val inStream: InputStream
        val outStream: FileOutputStream
        try {
            inStream = resources.assets.open("data/$cascadeFileName")

            val cascadeDir = getDir("cascade", Context.MODE_PRIVATE)
            cascadeFile = File(cascadeDir, "haarcascade_eye.xml")
            outStream = FileOutputStream(cascadeFile)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inStream.read(buffer).also { bytesRead = it } != -1) {
                outStream.write(buffer, 0, bytesRead)
            }

            inStream.close()
            outStream.close()
        } catch (e: IOException) {
            Log.i(TAG, "face cascade not found")
        }
    }

    /**
     * TODO NDK 3: opencv loader
     */
    private val baseLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded")
                    camView.enableView()
                }
                else -> super.onManagerConnected(status)
            }
        }
    }

    /**
     * TODO NDK 3: Load OpenCV if required
     */
    override fun onResume() {
        super.onResume()
        // camView.enableView()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback)
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
            loadCascadeFile()
        }
    }


    /**
     * TODO NDK 3: disable cam ui on pause
     */
    override fun onPause() {
        super.onPause()
        camView.disableView()
    }
}