package com.boliao.eod

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

/**
 * TODO ARCH 2.2: Manage login data with ViewModel and LiveData (i.e., use MVVM)
 * 1. create this ViewModel class that extends AndroidViewModel so as to be able to retrieve context
 *
 * TODO ARCH 3.6: Manage membership data with a Room
 * 1. modify this ViewModel class to take the player repo as input into the ctor
 * 2. change the extension to ViewModel() instead of AndroidViewMode(Application)
 * 3. create a factory that extends from ViewModelProvider.Factory
 *    NOTE This is due to the fact that we have ctor params.
 *    ViewModelProviders manage the lifecycle of VMs and we cannot create VMs by ourselves.
 *    So we need to provide a Factory to ViewModelProviders so that it knows how to create for us
 *    whenever we need an instance of it.
 */
class SplashViewModel(application: Application) // TODO ARCH 3.2:
//    : ViewModel() {
    : AndroidViewModel(application) { // to have context for shared prefs

    // live login status
    // NOTE that LiveData is a type of lifecycle-aware component
    // - manage functions that react to LifecycleOwners - e.g., Activity/Fragments/Services
    // - rather than the gazillion-responsibility dictatorship Activity class handling components
    //   using the onStart(), onResume() etc, now the responsibility falls on the individuals,
    //   like empowering students to do student-directed learning
    // - we can manually add lifecycle-aware components with
    //   someLifecycleOwner.getLifeCycle().addObserver(SomeLifecycleObserver())
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    // TODO ARCH 2.3: Manage login data with ViewModel and LiveData (i.e., use MVVM)
    // 1. move the shared prefs setup here
    // 2. create a Mutable and non-mutable LiveData pair to store the login status

    // TODO ARCH 3.6: Manage membership data with a Room
    // live member records from the Room DB

    /**
     * TODO ARCH 2.4: Manage login data with ViewModel and LiveData (i.e., use MVVM)
     * Simply provide a LiveData to track the login username.
     */

    /**
     * TODO ARCH 3.6: Manage membership data with a Room
     * Use a Room to manage the login data.
     */

    override fun onCleared() {
        super.onCleared()

        // remember to play safe, no leaks
        viewModelScope.cancel()
    }

    companion object {
        private val TAG = SplashViewModel::class.simpleName
    }
}

/**
 * TODO ARCH 3.6: Manage membership data with a Room
 * A factory to create the ViewModel properly.
 * Very boilerplatey code...
 */
