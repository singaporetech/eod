package com.boliao.eod;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import static java.lang.Thread.sleep;

/**
 * This is the splash screen that records who is playing.
 */
public class Splash extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "Splash";

    /**
     * TODO NDK
     * load native lib
     */
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * TODO NDK
     * declare native function
     */
    public native String stringFromJNI();

    // shared preferences setup
    public final static String PREF_FILENAME = "com.boliao.eod.prefs";
    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
		final Button playBtn = findViewById(R.id.play_btn);
        final AppCompatEditText usernameEdtTxt = findViewById(R.id.name_edtxt);
        final AppCompatTextView msgTxtView = findViewById(R.id.msg_txtview);
        final AppCompatTextView weatherTxtView = findViewById(R.id.weather_txtview);

        // setup shared preferences
        pref = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE);
        prefEditor = pref.edit();

        // TODO NDK
        // setup camera
        Toast.makeText(this, stringFromJNI(), Toast.LENGTH_SHORT).show();
        camView = findViewById(R.id.camview);
        camView.setVisibility(SurfaceView.VISIBLE);
        camView.setCvCameraViewListener(this);

        // TODO NETWORKING
        // getting permissions for location services
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "we need your permission lah deh", Toast.LENGTH_SHORT).show();

                Log.i(TAG, "LOCATION PERMISSION LOCATION");
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Log.i(TAG, "LOCATION PERMISSION GRANTED");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "we need your permission lah deh", Toast.LENGTH_SHORT).show();

                Log.i(TAG, "LOCATION PERMISSION LOCATION");
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Log.i(TAG, "LOCATION PERMISSION GRANTED");
        }

        // TODO NETWORKING
        // start the bounded service for networking
        startService(new Intent(this, WeatherService.class));

        // TODO NETWORKING
        // register local broadcast receiver to receive push data from weather service
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String forecastStr = intent.getStringExtra(WeatherService.WEATHER_BROADCAST_EXTRAS_FORECAST);
                Log.i(TAG, "RECEIVED forecast = " + forecastStr);

                // update UI here
                weatherTxtView.setText(forecastStr);
            }
        }, new IntentFilter(WeatherService.WEATHER_BROADCAST_ACTION));

		// start game on click "PLAY"
		playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO SERVICES 1
                // 1: just store in preferences
                String usernameStr = usernameEdtTxt.getText().toString();
                String existingStr = pref.getString(usernameStr, "-----");
                if (usernameStr.equals(existingStr)) {
                    msgTxtView.setText("player exists, please choose another name");
                }
                else {
                    msgTxtView.setText("starting game, pls wait...");

                    // TODO SERVICES 2: what if this needs some intensive processing
                    // encrypt the username using some funky algo

                    // defer the encryption to a background service
                    UserEncryptionService.startActionEncrypt(v.getContext(), usernameStr);

                    // start the game activity
                    Intent intent = new Intent(v.getContext(), AndroidLauncher.class);
                    startActivity(intent);
                }

                // TODO SERVICES 3: WHAT IF need to check if username is banned from server and come back to UI
            }
        });


    }

    /**
     * TODO NDK
     * implement cam callbacks
     */
    @Override
    public void onCameraViewStarted(int width, int height) {

    }
    @Override
    public void onCameraViewStopped() {

    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }

    CameraBridgeViewBase camView;
    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded");
                    camView.enableView();
                    break;

                default:
                    super.onManagerConnected(status);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void disableCam() {
        if (camView != null)
            camView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disableCam();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableCam();
    }
}
