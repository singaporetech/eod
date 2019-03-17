/**
 * WEEK11 LECTURE:
 * 1. demo NDK face detection
 */
package com.boliao.eod;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import static java.lang.Thread.sleep;

/**
 * This is the splash view that records who is playing.
 */
public class Splash extends AppCompatActivity {
    private static final String TAG = "Splash";
    // TODO NDK 0: install required dependencies (from Android Studio SDK Tools)
    // - NDK: Android toolset to communicate with native code
    // - CMake: native build tool
    // - LLDB: native code debugger

    // TODO NDK 1: create the native code and build configuration
    // - create a src/cpp directory
    // - create a new .cpp file
    // - create a CMake build script called CMakeLists.txt (https://developer.android.com/studio/projects/configure-cmake.html)
    // - add CMake path in gradle (you can right click on folder and let IDE do it)

    // TODO NDK 2: create a test method in native to receive a string and show it
    // - declare a native function you want to write in C/C++
    // - write the method in a cpp file
    // - load native lib and declare native methods
    // - receive string from native function and display it in a toast
    // - try and debug within native using <android/log.h>
    static {
        System.loadLibrary("native-lib");
    }
    public native String getNativeString();

    // TODO NDK 3: use OpenCV C lib to do face recognition

    // shared preferences setup
    public final static String PREF_FILENAME = "com.boliao.eod.prefs";
    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private Intent startAndroidLauncher;

    public void launchGame() {
        startActivity(startAndroidLauncher);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // TODO NDK 2: show the string from native
        Toast.makeText(this, getNativeString(), Toast.LENGTH_SHORT).show();

        // init launch game intent
        startAndroidLauncher = new Intent(Splash.this, AndroidLauncher.class);

        // get refs to UI components
		final Button playBtn = findViewById(R.id.play_btn);
        final AppCompatEditText usernameEdtTxt = findViewById(R.id.name_edtxt);
        final AppCompatTextView msgTxtView = findViewById(R.id.msg_txtview);
        final AppCompatTextView weatherTxtView = findViewById(R.id.weather_txtview);

        // setup shared preferences
        pref = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE);
        prefEditor = pref.edit();

        // show splash text
        msgTxtView.setText(R.string.welcome_note);

        // TODO THREADING 2: create a persistent weather widget
        // - WeatherRepo is already nicely linked up in MVVM with SplashViewModel
        // - implement background weather fetching in WeatherRepo
        // Q: Do I (Splash Activity) need to know about WeatherRepo?
        ViewModel splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        ((SplashViewModel) splashViewModel).getWeatherData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                weatherTxtView.setText(s);
            }
        });

		// start game on click "PLAY"
		playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO SERVICES 1: check if username is already taken
                // - if username exists, set msgTxtView to "player exists..."
                // - else, set msgTxtView to "starting game, pls wait"

                String username = usernameEdtTxt.getText().toString();
                if(pref.contains(username)) {
                    msgTxtView.setText("Name already exists!");
                }
                else {
                    // Store  username to survive app destruction
                    // DEPRECATED due to encryption below
                    /*
                    msgTxtView.setText("Starting game...");
                    prefEditor.putString(username, username);
                    prefEditor.commit();
                    */

                    // TODO SERVICES 2: what if this needs some intensive processing
                    // - e.g., pseudo-encrypt the username using some funky algo
                    // - store the encrypted username in shared prefs
                    // - UI should not lag or ANR
                    // SOLN: defer processing to an IntentService: do some heavy lifting w/o
                    // UI then shutdown the service
                    // - note that the WorkManager can also accomplish this
                    NameCryptionService.startActionFoo(Splash.this, username);

                    // TODO THREADING 1: what if now, I want this result to be shown on UI
                    // - I know know this encryption the most takes 5secs
                    // - user needs to know result of what happened to his name anyway
                    // SOLN: use AsyncTask

                    new EncryptTask(Splash.this).execute(username);
                    // launch the game
                }

                // TODO SERVICES n: goto AndroidLauncher
            }
        });
    }

    /**
     * AsyncTask to "encrypt" username
     * - heavy lifting in the background to be posted back to UI
     * - static class so as to prevent leaks
     * - need a ref to update UI thread, so use WeakReference (a.k.a. shared_ptr)
     * - onProgressUpdate(Integer... progress) left as an exercise
     * - note: publishProgress(Integer) is in built to pass progress to above from doInBackground
     */
    private static class EncryptTask extends AsyncTask<String, Void, Boolean> {
        // this is to get all the UI elements
        // - use weak reference so that it does not leak mem when activity gets killed
        WeakReference<Activity> wr_act;

        public EncryptTask(Activity act) {
            this.wr_act = new WeakReference<Activity>(act);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity act = wr_act.get();
            if (act != null) {
                ((TextView)act.findViewById(R.id.msg_txtview)).setText("encrypting");
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                sleep(3000);
                // do something to the strings
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            Activity act = wr_act.get();
            if (act != null) {
                ((TextView)act.findViewById(R.id.msg_txtview)).setText("The encryption is:" + b);
                ((Splash)act).launchGame();
            }
        }
    }
}
