package com.boliao.eod;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static java.lang.Thread.sleep;

/**
 * This is the splash screen that records who is playing.
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
		final Button playBtn = findViewById(R.id.play_btn);
        final AppCompatEditText usernameEdtTxt = findViewById(R.id.name_edtxt);
        final AppCompatTextView msgTxtView = findViewById(R.id.msg_txtview);

        // setup shared preferences
        pref = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE);
        prefEditor = pref.edit();

		// start game on click "PLAY"
		playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO in LECTURE
                // 1: just store in preferences
                String usernameStr = usernameEdtTxt.getText().toString();
                String existingStr = pref.getString(usernameStr, "-----");
                if (usernameStr.equals(existingStr)) {
                    msgTxtView.setText("player exists, please choose another name");
                }
                else {
                    msgTxtView.setText("starting game, pls wait...");

                    // TODO 2: what if this needs some intensive processing
                    // encrypt the username using some funky algo

                    // defer the encryption to a background service
                    UserEncryptionService.startActionEncrypt(v.getContext(), usernameStr);

                    // start the game activity
                    Intent intent = new Intent(v.getContext(), AndroidLauncher.class);
                    startActivity(intent);
                }

                // TODO 3: WHAT IF need to check if username is banned from server and come back to UI
            }
        });
    }
}
