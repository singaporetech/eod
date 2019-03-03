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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * This is the splash view that records who is playing.
 */
public class Splash extends AppCompatActivity {
    private static final String TAG = "Splash";

    // shared preferences setup
    public final static String PREF_FILENAME = "com.boliao.eod.prefs";
    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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

        // TODO THREADING 1: create a persistent weather widget
        // - always updating regularly (confirm < 15min) from online API
        // - not expecting to pause it at any point until user logs in, where we'll stop it manually
        // - ideally want updates even if navigate away (or even destroyed)
        // SOLN: use a Started Service driven by a HandlerThread
        //  - Recurring WM cannot as min 15 mins interval
        //  - IntentService not ideal as we need always on
        //  - ThreadPoolExecutor overkill as we only need one series of sequential work
        //  - pure AsyncTask too much creation/destroying
        //  - pure HandlerThread in VM will be killed when view killed

		// start game on click "PLAY"
		playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dummy action
                Intent startAndroidLauncher = new Intent(Splash.this, AndroidLauncher.class);

                // TODO SERVICES 1: check if username is already taken
                // - if username exists, set msgTxtView to "player exists..."
                // - else, set msgTxtView to "starting game, pls wait"

                String username = usernameEdtTxt.getText().toString();
                if(pref.contains(username)) {
                    msgTxtView.setText("Name already exists!");
                }
                else {
                    msgTxtView.setText("Starting game...");

                    // Store  username  to survive app destruction
                    // DEPRECATED due to encryption below
                    /*
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
                    startActivity(startAndroidLauncher);

                    // TODO THREADING 1: what if now, I want this result to be shown on UI
                    // - I know know this encryption the most takes 5secs
                    // - user needs to know result of what happened to his name anyway
                    // SOLN: use AsyncTask
                }

                // TODO SERVICES n: goto AndroidLauncher
            }
        });
    }
}
