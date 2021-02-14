package com.boliao.eod

import android.app.Application

class EODApp: Application() {
    val db by lazy { PlayerDB.getDatabase(this)}
    val repo by lazy { PlayerRepo(db.playerDAO()) }
}
