package com.boliao.eod

import android.app.Application

class App: Application() {
    val playerDB by lazy { PlayerDB.getDatabase(this)}
    val playerRepo by lazy { PlayerRepo(playerDB.playerDAO())}
}