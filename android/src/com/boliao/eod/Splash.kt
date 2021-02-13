/**
 * # WHAT IS THIS?
 * Example android app EOD using a mix of libraries, e.g., libgdx for graphics.
 * 1. browse through the code structure
 * 2. highlight expected commenting conventions
 * 
 * # WEEK06: Putting (some of) it all together
 * This week we will look at some common Android Architecture Components through a running
 * example of a simple login feature that we will try to implement.
 * 0. see the necessary artifacts in build.gradle
 * 1. create a login view in Splash Activity and manage the login in the activity 
 * 2. create a ViewModel component to manage the login with a LiveData component (i.e., use MVVM)
 * 3. create a Room component to manage the login data more comprehensively
 *
 * # Extras:
 * 1. Managing Sprints through github
 */
package com.boliao.eod

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.boliao.eod.databinding.ActivitySplashBinding
import kotlinx.coroutines.*

/**
 * Splash View to show the entry screen of the app.
 * - shows some status info
 * - handles user login to enter the game
 */
class Splash : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var startAndroidLauncher: Intent
    private lateinit var binding:ActivitySplashBinding

    // TODO ARCH 3.2: Manage membership data with a Room
    // 1. lazy init the Room DB
    // 2. lazy init the player repo with the DAO from the DB
    // This should be done at the application level in AndroidLauncher
    val playerDB by lazy { PlayerDB.getDatabase(this)}
    val playerRepo by lazy { PlayerRepo(playerDB.playerDAO())}

    /**
     * Helper function to start the game.
     * Android launcher will start the game state service.
     */
    fun launchGame() {
        startActivity(startAndroidLauncher)
    }

    /**
     * Setup all the UI elements and their connections with the VM.
     * @param savedInstanceState the usual bundle of joy
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init intent to launch the game
        // NOTE I have more comments than necessary for demo purposes
        startAndroidLauncher = Intent(this@Splash, AndroidLauncher::class.java)

        // show splash text by default
        binding.msgTxtview.setText(R.string.welcome_note)

        // TODO ARCH 1.2: Manage login data in view
        // 1. get shared prefs using the filename and set mode to private for this app
        // 2. set the play btn's onClickListener to handle login
        //    only allow login for unique users
        // 3. show status of login on screen
        // 4. do a rotation and see what happens
//        pref = getSharedPreferences(PREF_FILENAME, MODE_PRIVATE)
//        binding.playBtn.setOnClickListener {
//            val username = binding.nameEdtxt.text.toString()
//            if (pref.contains(username)) {
//                binding.msgTxtview.text = "Have user liao lah..."
//            }
//            else {
//                pref.edit().putString(username, username).apply()
//                binding.msgTxtview.text = "logging in..."
//            }
//        }

        // TODO ARCH 2.1: Manage login data with ViewModel and LiveData (i.e., use MVVM)
        // 1. create a ViewModel (VM) component for this Splash View
        // 2. move the login data mgt to the VM
        // 3. reset the play btn's onClickListener to handle login through the VM
        // 4. create a LiveData component to hold the login status in the VM
        // 5. observe the login status in this View
        // start game on click "PLAY"

        // TODO ARCH 3.1: Manage membership data with a Room
        // 0. add input field(s) to the layout xml
        // 1. create an entity class to represent a single user record with more fields
        // 2. create a DAO to handle queries
        // 3. create a Room DB
        // 4. create a Repo to manage the database
        // 5. modify the VM to include the repo as input to the ctor
        // 6. init Room DB and repo at app level
        // 7. manage the database through the VM
        val splashViewModel: SplashViewModel by viewModels {
            SplashViewModelFactory(playerRepo)
        }
        binding.playBtn.setOnClickListener {
            splashViewModel.login(
                    binding.nameEdtxt.text.toString(),
                    if (binding.ageEdtxt.text.toString() == "") 0
                    else binding.ageEdtxt.text.toString().toInt()
            )
        }
        splashViewModel.loginStatus.observe(this, {
            if (it) {
                binding.msgTxtview.text = "logging in..."
                launchGame()
            }
            else
                binding.msgTxtview.text = "Name OREDI exists lah..."
        })

        // provide a way to stop the service
        binding.exitBtn.setOnClickListener {
            stopService(AndroidLauncher.startServiceIntent)
            finish()
        }
    }

    companion object {
        private const val TAG = "Splash"

        // TODO ARCH 1.1: Manage login data in view
        // 1. create unique FILENAME const to reference dataset
        // 2. create a SharedPreferences var to manage data
        const val PREF_FILENAME = "com.boliao.eod.prefs"
        private lateinit var pref: SharedPreferences
    }
}
