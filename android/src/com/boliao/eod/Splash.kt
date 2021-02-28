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
 *
 * # WEEK08: SERVICES
 * Run through several use cases for different background processing requirements.
 * 1. revise persistent storage options to determine if username exists.
 * 2. create a method in the view to generate a hard pw using some pseudo-cpu-intensive algo
 * 3. now use the VM to perform the pw generation task
 * 4. now use an IntentService for the pw generation task
 * 5. observe the started and bound service: GameStateService
 * 6. configure notifications for the GameStateService when bugs spawn
 * 7. convert the started service to a foreground service
 * 8. create a scheduled service (once app boots) to remind user to charge the phone periodically
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
 * - NOTE that some it may be more precise to refer to this as the Controller and the XML as the view
 * - MVCVM? :/
 * - shows some status info
 * - handles user login to enter the game
 */
class Splash : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var startAndroidLauncher: Intent
    private lateinit var binding:ActivitySplashBinding

    // TODO ARCH 3: Manage membership data with a Room
    // 1. lazy init the Room DB
    // 2. lazy init the player repo with the DAO from the DB
    // This should be done at the application level in
    private val splashViewModel: SplashViewModel by viewModels {
        SplashViewModelFactory((application as EODApp).repo)
    }

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

        // init launch game intent
        // NOTE I have more comments than necessary for demo purposes
        startAndroidLauncher = Intent(this@Splash, AndroidLauncher::class.java)

        // show splash text by default
        binding.msgTxtview.setText(R.string.welcome_note)

        binding.playBtn.setOnClickListener {
            val name = binding.nameEdtxt.text.toString()
            val age =
                    if (binding.ageEdtxt.text.toString() == "") 0
                    else binding.ageEdtxt.text.toString().toInt()

            // TODO SERVICES 2.1: brute force method of having the cpu-intensive pw generator here
            // 1. simulate naive approach to perform an 8-sec long pseudo-encrypt method here
            // 2. extend the Player, PlayerDAO, PlayerRepo to have access to a pw field
            // 3. modify the login method in VM to take in a pw as well to add to the db
            // 4. observe the UI when we run it this way...
//            val pw = getEncryptedPw(name)

            // TODO SERVICES 3.1: slightly better method by having the pw generator in the VM
            // 1. shift the pw generator method into the VM
            // 2. omit the pw arg into the login() in VM
//            val pw = null
//            splashViewModel.login(
////                    this,
//                    name, age
////                    , pw
//            )

            // TODO SERVICES 4.1: an even better pw generator using an IntentService
            // 1. create an IntentService for the encryption task
            // 2. include context in the login(args..) to start the service
            // QNS: so when do we use services?
            // Note that the WorkManager is the preferred way to do this now
            splashViewModel.login(this, name, age)
        }

        // observe login status changes from the VM
        splashViewModel.loginStatus.observe(this, {
            if (it) {
                binding.msgTxtview.text = "logging in..."

                // NOTE that launchGame is launching a View so should be here
                launchGame()
            } else
                binding.msgTxtview.text = "Name OREDI exists lah..."
        })

        // provide a way to stop the service
        binding.exitBtn.setOnClickListener {
            stopService(AndroidLauncher.startServiceIntent)
            finish()
        }
    }

    /**
     * TODO SERVICES 2.2: write pseudo-encryption function.
     *
     * A function that simply sleeps for 5s to mock a cpu-intensive task
     * It also does some amazing manip to the name string
     * @param name of the record to generate pw for
     * @return (pseudo-)encrypted pw String
     */
    private fun getEncryptedPw(name: String): String {
        Thread.sleep(8000)
        return name + "888888"
    }

    companion object {
        private val TAG = Splash::class.simpleName
    }
}
